package gabia.gvote.service;

import gabia.gvote.EntityFactory;
import gabia.gvote.dto.AgendaCreateRequestDTO;
import gabia.gvote.dto.AgendaPageDTO;

import gabia.gvote.dto.SessionMemberAuthDTO;
import gabia.gvote.entity.*;
import gabia.gvote.repository.AgendaRepository;
import gabia.gvote.repository.MemberAuthRepository;
import gabia.gvote.repository.MemberRepository;
import gabia.gvote.repository.VoteRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class AgendaServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AgendaService agendaService;
    @Autowired
    private MemberAuthRepository memberAuthRepository;


    @DisplayName("agenda 페이징 요청이 수행되고 올바른 값을 가진 DTO가 반환되어야 한다.")
    @Test
    void agendaPaginationTest() {
        //given
        Member member = EntityFactory.generateMember();
        memberRepository.save(member);

        Agenda agenda1 = EntityFactory.generateAgenda(member);
        Agenda agenda2 = EntityFactory.generateAgenda(member);
        Agenda agenda3 = EntityFactory.generateAgenda(member);
        Agenda agenda4 = EntityFactory.generateAgenda(member);
        Agenda agenda5 = EntityFactory.generateAgenda(member);
        agendaRepository.saveAll(List.of(agenda1, agenda2, agenda3, agenda4, agenda5));

        Vote vote1 = EntityFactory.generateBeforeUnlimitVote(agenda1);
        Vote vote2 = EntityFactory.generateBeforeUnlimitVote(agenda2);
        Vote vote3 = EntityFactory.generateBeforeUnlimitVote(agenda3);
        Vote vote4 = EntityFactory.generateBeforeUnlimitVote(agenda4);
        Vote vote5 = EntityFactory.generateBeforeUnlimitVote(agenda5);
        voteRepository.saveAll(List.of(vote1, vote2, vote3, vote4, vote5));

        PageRequest pageable = PageRequest.of(0, 10);

        //when
        AgendaPageDTO agendaPageDTO = agendaService.agendaPagination(pageable);

        //then
        Assertions.assertThat(agendaPageDTO.getCount()).isEqualTo(5);
        List<AgendaPageDTO.SingleAgenda> agendas = agendaPageDTO.getAgendas();

        Assertions.assertThat(agendas.get(0).getAgendaId()).isEqualTo(agenda1.getAgendaId());
        Assertions.assertThat(agendas.get(1).getAgendaId()).isEqualTo(agenda2.getAgendaId());
        Assertions.assertThat(agendas.get(2).getAgendaId()).isEqualTo(agenda3.getAgendaId());
        Assertions.assertThat(agendas.get(3).getAgendaId()).isEqualTo(agenda4.getAgendaId());
        Assertions.assertThat(agendas.get(4).getAgendaId()).isEqualTo(agenda5.getAgendaId());

        Assertions.assertThat(agendas.get(0).getAgendaSubject()).isEqualTo(agenda1.getAgendaSubject());
        Assertions.assertThat(agendas.get(1).getAgendaSubject()).isEqualTo(agenda2.getAgendaSubject());
        Assertions.assertThat(agendas.get(2).getAgendaSubject()).isEqualTo(agenda3.getAgendaSubject());
        Assertions.assertThat(agendas.get(3).getAgendaSubject()).isEqualTo(agenda4.getAgendaSubject());
        Assertions.assertThat(agendas.get(4).getAgendaSubject()).isEqualTo(agenda5.getAgendaSubject());

        Assertions.assertThat(agendas.get(0).getVoteId()).isEqualTo(vote1.getVoteId());
        Assertions.assertThat(agendas.get(0).getVoteStartAt()).isEqualTo(vote1.getStartAt());
        Assertions.assertThat(agendas.get(1).getVoteId()).isEqualTo(vote2.getVoteId());
        Assertions.assertThat(agendas.get(1).getVoteStartAt()).isEqualTo(vote2.getStartAt());
        Assertions.assertThat(agendas.get(2).getVoteId()).isEqualTo(vote3.getVoteId());
        Assertions.assertThat(agendas.get(2).getVoteStartAt()).isEqualTo(vote3.getStartAt());
        Assertions.assertThat(agendas.get(3).getVoteId()).isEqualTo(vote4.getVoteId());
        Assertions.assertThat(agendas.get(3).getVoteStartAt()).isEqualTo(vote4.getStartAt());
        Assertions.assertThat(agendas.get(4).getVoteId()).isEqualTo(vote5.getVoteId());
        Assertions.assertThat(agendas.get(4).getVoteStartAt()).isEqualTo(vote5.getStartAt());
    }

    @DisplayName("안건 생성 요청이 완료되면 안건과 투표가 조회되어야 한다")
    @Test
    void create_success() {
        //given
        Member adminMember = EntityFactory.generateMember();
        MemberAuth adminMemberAuth = EntityFactory.generateAdminMemberAuth(adminMember);
        memberRepository.save(adminMember);
        memberAuthRepository.save(adminMemberAuth);
        LocalDateTime now = LocalDateTime.now();
        AgendaCreateRequestDTO agendaCreateRequestDTO = new AgendaCreateRequestDTO("test_sub", "test_cont", now, now.plusDays(1), VoteGubun.UNLIMITED, 100L);
        SessionMemberAuthDTO sessionMemberAuthDTO = new SessionMemberAuthDTO(adminMember.getMemberId(), MemberGubun.ADMIN);
        //when
        Long savedAgendaId = agendaService.create(sessionMemberAuthDTO, agendaCreateRequestDTO);
        //then
        Agenda findAgenda = agendaRepository.findById(savedAgendaId).get();
        Vote findVote = voteRepository.findByAgendaId(savedAgendaId).get();

        Assertions.assertThat(findAgenda.getAgendaSubject()).isEqualTo(agendaCreateRequestDTO.getAgendaSubject());
        Assertions.assertThat(findAgenda.getAgendaContent()).isEqualTo(agendaCreateRequestDTO.getAgendaContent());
        Assertions.assertThat(findAgenda.getMember().getMemberId()).isEqualTo(adminMember.getMemberId());
        Assertions.assertThat(findVote.getAgenda().getAgendaId()).isEqualTo(savedAgendaId);
        Assertions.assertThat(findVote.getStartAt()).isEqualTo(agendaCreateRequestDTO.getVoteStartAt());
        Assertions.assertThat(findVote.getCloseAt()).isEqualTo(agendaCreateRequestDTO.getVoteCloseAt());
        Assertions.assertThat(findVote.getVoteGubun()).isEqualTo(agendaCreateRequestDTO.getVoteGubun());
        Assertions.assertThat(findVote.getRemainAvailableVoteCount()).isEqualTo(agendaCreateRequestDTO.getAvailableVoteCount());
    }

    @DisplayName("존재하지 않는 안건의 삭제를 요청하면 오류가 발생해야 한다.")
    @Test
    void delete_invalidAgendaId() {
        //given
        Long invalidAgendaId = 121234531246123563L;
        //when //then
        Assertions.assertThatThrownBy(() -> agendaService.delete(invalidAgendaId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("관리자 회원이 안건 삭제를 요청하면 삭제되어야 한다.")
    @Test
    void delete_test() {
        //given
        Member adminMember = EntityFactory.generateMember();
        MemberAuth memberAuth = EntityFactory.generateAdminMemberAuth(adminMember);
        Agenda agenda = EntityFactory.generateAgenda(adminMember);
        Vote vote = EntityFactory.generateBeforeUnlimitVote(agenda);
        memberRepository.save(adminMember);
        memberAuthRepository.save(memberAuth);
        agendaRepository.save(agenda);
        voteRepository.save(vote);

        //when
        agendaService.delete(agenda.getAgendaId());

        //then
        Assertions.assertThat(agendaRepository.findById(agenda.getAgendaId()).isEmpty()).isTrue();
    }

    @DisplayName("투표가 진행 중이거나 종료된 투표의 안건을 삭제 요청하면 오류가 발생해야 한다.")
    @Test
    void delete_IngAndDoneVote() {
        //given
        Member adminMember = EntityFactory.generateMember();
        MemberAuth memberAuth = EntityFactory.generateAdminMemberAuth(adminMember);
        Agenda agenda = EntityFactory.generateAgenda(adminMember);
        Vote vote = EntityFactory.generateInglimitVote(agenda);
        memberRepository.save(adminMember);
        memberAuthRepository.save(memberAuth);
        agendaRepository.save(agenda);
        voteRepository.save(vote);

        //when //then
        Assertions.assertThatThrownBy(() -> agendaService.delete(agenda.getAgendaId()))
                .isInstanceOf(IllegalStateException.class);
    }


}
