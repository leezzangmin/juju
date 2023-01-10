package gabia.gvote.service;

import gabia.gvote.EntityFactory;
import gabia.gvote.dto.AgendaPageDTO;

import gabia.gvote.entity.Agenda;
import gabia.gvote.entity.Member;
import gabia.gvote.entity.Vote;
import gabia.gvote.repository.AgendaRepository;
import gabia.gvote.repository.MemberRepository;
import gabia.gvote.repository.VoteRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class AgendaServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AgendaRepository agendaRepository;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    AgendaService agendaService;


    @DisplayName("agenda 페이징 요청이 수행되고 올바른 값을 가진 DTO가 반환되어야 한다.")
    @Test
    void agendaPaginationTest() {
        //given
        Member member = EntityFactory.generateNormalMember();
        memberRepository.save(member);

        Agenda agenda1 = EntityFactory.generateAgenda(member);
        Agenda agenda2 = EntityFactory.generateAgenda(member);
        Agenda agenda3 = EntityFactory.generateAgenda(member);
        Agenda agenda4 = EntityFactory.generateAgenda(member);
        Agenda agenda5 = EntityFactory.generateAgenda(member);
        agendaRepository.saveAll(List.of(agenda1, agenda2, agenda3, agenda4, agenda5));

        Vote vote1 = EntityFactory.generateBeforeUnlimitVote(agenda1, member);
        Vote vote2 = EntityFactory.generateBeforeUnlimitVote(agenda2, member);
        Vote vote3 = EntityFactory.generateBeforeUnlimitVote(agenda3, member);
        Vote vote4 = EntityFactory.generateBeforeUnlimitVote(agenda4, member);
        Vote vote5 = EntityFactory.generateBeforeUnlimitVote(agenda5, member);
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


}
