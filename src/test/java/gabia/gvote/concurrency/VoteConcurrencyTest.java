package gabia.gvote.concurrency;

import gabia.gvote.EntityFactory;
import gabia.gvote.dto.VoteAdminResponseDTO;
import gabia.gvote.dto.VoteCreateRequestDTO;
import gabia.gvote.entity.*;
import gabia.gvote.repository.*;
 import gabia.gvote.service.VoteService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

 @SpringBootTest
public class VoteConcurrencyTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private MemberAuthRepository memberAuthRepository;
    @Autowired
    private VoteHistoryRepository voteHistoryRepository;
    @Autowired
    private VoteResultRepository voteResultRepository;
    @Autowired
    private VoteService voteService;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    private final int threadCount = 100;

    private Vote limitVote;
    private Vote unlimitVote;
    private Vote doneVote;
    private Member member;

    @BeforeEach
    public void beforeEach() {
        Member adminMember = EntityFactory.generateMember();
        MemberAuth adminMemberAuth = EntityFactory.generateAdminMemberAuth(adminMember);
        Agenda agenda = EntityFactory.generateAgenda(adminMember);
        limitVote = EntityFactory.generateInglimitVote(agenda);
        unlimitVote = EntityFactory.generateIngUnlimitVote(agenda);
        doneVote = EntityFactory.generateDoneUnlimitVote(agenda);
        member = EntityFactory.generateMember();
        VoteHistory voteHistory = EntityFactory.generate10AbstentionVoteHistory(doneVote, member);

        memberRepository.saveAll(List.of(adminMember, member));
        memberAuthRepository.save(adminMemberAuth);
        agendaRepository.save(agenda);
        voteRepository.saveAll(List.of(limitVote, unlimitVote, doneVote));
        voteHistoryRepository.save(voteHistory);

        executorService = Executors.newFixedThreadPool(threadCount);
        countDownLatch = new CountDownLatch(threadCount);
    }

    @AfterEach
    public void afterEach() {
        voteResultRepository.deleteAll();
        voteHistoryRepository.deleteAll();
        voteRepository.deleteAll();
        agendaRepository.deleteAll();
        memberAuthRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @DisplayName("100명의 유저가 의결권 1개씩 동시에 90개 제한인 투표에 요청을 보내도 미리 설정된 제한된 의결권 숫자(90)까지만 허용되어야 한다.")
    @Test
    void limitedVoteConcurrencyTest() throws InterruptedException {
        //given
        VoteCreateRequestDTO voteCreateRequestDTO = new VoteCreateRequestDTO(1, VoteHistoryActionGubun.YES);

        //when
        IntStream.range(0, threadCount)
                        .forEach(e -> executorService.submit(() -> {
                            try {
                                voteService.vote(member.getMemberId(), limitVote.getVoteId(), voteCreateRequestDTO);
                            } catch(Exception ex) {
                                System.out.println("ex.getMessage() = " + ex.getMessage());
                            } finally {
                                countDownLatch.countDown();
                            }
        }));
        countDownLatch.await();

        //then
        Vote findVote = voteRepository.findById(limitVote.getVoteId()).get();
        Assertions.assertThat(findVote.getRemainAvailableVoteCount()).isEqualTo(0L);
    }

     @DisplayName("100명의 유저가 의결권 1개씩 무제한 투표에 요청을 보내면 100개 모두 허용되어야 한다.")
     @Test
     void unlimitedVoteConcurrencyTest() throws InterruptedException {
         //given
         VoteCreateRequestDTO voteCreateRequestDTO = new VoteCreateRequestDTO(1, VoteHistoryActionGubun.YES);

         //when
         IntStream.range(0, threadCount)
                 .forEach(e -> executorService.submit(() -> {
                     try {
                         voteService.vote(member.getMemberId(), unlimitVote.getVoteId(), voteCreateRequestDTO);
                     } catch(Exception ex) {
                         System.out.println("ex.getMessage() = " + ex.getMessage());
                     } finally {
                         countDownLatch.countDown();
                     }
                 }));
         countDownLatch.await();

         //then
         int voteSize = voteHistoryRepository.findAllByReferenceVoteId(unlimitVote.getVoteId()).size();
         Assertions.assertThat(voteSize).isEqualTo(threadCount);
     }


     @DisplayName("100명의 유저가 동시에 투표결과를 조회해도 캐시 결과는 하나만 존재해야 한다.")
     @Disabled
     @Test
     void voteCacheOnlyOne() throws InterruptedException {
        //given
         Long voteId = doneVote.getVoteId();
         System.out.println("voteId = " + voteId);
         //when
         IntStream.range(0, threadCount)
                 .forEach(e -> executorService.submit(() -> {
                     try {
                         voteService.findOne(voteId);
                         System.out.println("e = " + e);
                         Thread.sleep(1000);
                     } catch (InterruptedException ex) {
                         throw new RuntimeException(ex);
                     } finally {
                         countDownLatch.countDown();
                     }
                 }));
         countDownLatch.await();

         //then
         VoteAdminResponseDTO voteIdSimpleAdminDTO = voteResultRepository.findByReferenceVoteIdSimpleAdminDTO(voteId);
     }

}
