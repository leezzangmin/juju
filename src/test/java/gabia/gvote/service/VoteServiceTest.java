package gabia.gvote.service;

import gabia.gvote.EntityFactory;
import gabia.gvote.dto.VoteNormalResponseDTO;
import gabia.gvote.dto.VoteResponseDTO;
import gabia.gvote.entity.*;
import gabia.gvote.repository.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class VoteServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberAuthRepository memberAuthRepository;
    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private VoteService voteService;
    @Autowired
    private VoteHistoryRepository voteHistoryRepository;


    @DisplayName("일반 회원이 종료된 투표를 조회하면 정확한 통계의 투표결과가 반환되어야 한다.")
    @Test
    void findOne_normalMember() {
        //given
        Member adminMember = EntityFactory.generateMember();
        MemberAuth adminMemberAuth = EntityFactory.generateAdminMemberAuth(adminMember);
        Agenda agenda = EntityFactory.generateAgenda(adminMember);
        Vote vote = EntityFactory.generateDoneUnlimitVote(agenda);
        Member member1 = EntityFactory.generateMember();
        MemberAuth memberAuth = EntityFactory.generateNormalMemberAuth(member1);
        Member member2 = EntityFactory.generateMember();
        Member member3 = EntityFactory.generateMember();
        VoteHistory voteHistory1 = EntityFactory.generate10YesVoteHistory(vote, member1);
        VoteHistory voteHistory2 = EntityFactory.generate10NoVoteHistory(vote, member2);
        VoteHistory voteHistory3 = EntityFactory.generate10AbstentionVoteHistory(vote, member3);
        memberRepository.saveAll(List.of(adminMember, member1, member2, member3));
        memberAuthRepository.saveAll(List.of(adminMemberAuth, memberAuth));
        agendaRepository.save(agenda);
        voteRepository.save(vote);
        voteHistoryRepository.saveAll(List.of(voteHistory1, voteHistory2, voteHistory3));

        //when
        VoteResponseDTO voteResponseDTO = voteService.findOne(member1.getMemberId(), vote.getVoteId());

        //then
        VoteNormalResponseDTO convertedVoteResponseDTO = (VoteNormalResponseDTO) voteResponseDTO;
        Assertions.assertThat(convertedVoteResponseDTO.getVoteId()).isEqualTo(vote.getVoteId());
        Assertions.assertThat(convertedVoteResponseDTO.getYesCount()).isEqualTo(10L);
        Assertions.assertThat(convertedVoteResponseDTO.getNoCount()).isEqualTo(10L);
        Assertions.assertThat(convertedVoteResponseDTO.getAbstentionCount()).isEqualTo(10L);
    }

    @DisplayName("존재하지 않는 id의 회원이 조회를 요청하면 오류가 발생해야 한다.")
    @Test
    void findOne_invalidMemberId() {
        //given
        Long invalidMemberId = 12347813946579143L;
        //when
        //then
        Assertions.assertThatThrownBy(() -> voteService.findOne(invalidMemberId, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않는 id의 투표를 조회 요청하면 오류가 발생해야 한다.")
    @Test
    void findOne_invalidVoteId() {
        //given
        Member member = EntityFactory.generateMember();
        MemberAuth memberAuth = EntityFactory.generateNormalMemberAuth(member);
        memberRepository.save(member);
        memberAuthRepository.save(memberAuth);

        Long invalidVoteId = 143578913456L;

        //when //then
        Assertions.assertThatThrownBy(() -> voteService.findOne(member.getMemberId(), invalidVoteId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("아직 끝나지 않은 투표를 조회 요청하면 오류가 발생해야 한다.")
    @Test
    void findOne_invalidVoteStatus() {
        //given
        Member member = EntityFactory.generateMember();
        MemberAuth memberAuth = EntityFactory.generateNormalMemberAuth(member);
        Member adminMember = EntityFactory.generateMember();
        MemberAuth adminMemberAuth = EntityFactory.generateAdminMemberAuth(adminMember);
        Agenda agenda = EntityFactory.generateAgenda(adminMember);
        Vote vote = EntityFactory.generateBeforeUnlimitVote(agenda);

        memberRepository.saveAll(List.of(adminMember,member));
        memberAuthRepository.saveAll(List.of(memberAuth, adminMemberAuth));
        agendaRepository.save(agenda);
        voteRepository.save(vote);

        //when //then
        Assertions.assertThatThrownBy(() -> voteService.findOne(member.getMemberId(), vote.getVoteId()))
                .isInstanceOf(IllegalStateException.class);
    }

}
