package gabia.gvote.service;

import gabia.gvote.dto.AgendaCreateRequestDTO;
import gabia.gvote.dto.AgendaPageDTO;
import gabia.gvote.entity.Agenda;
import gabia.gvote.entity.MemberAuth;
import gabia.gvote.entity.MemberGubun;
import gabia.gvote.entity.Vote;
import gabia.gvote.repository.AgendaRepository;
import gabia.gvote.repository.MemberAuthRepository;
import gabia.gvote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final VoteRepository voteRepository;
    private final MemberAuthRepository memberAuthRepository;

    @Transactional(readOnly = true)
    public AgendaPageDTO agendaPagination(Pageable pageable) {
        List<Long> agendaIds = agendaRepository.findIdsByPage(pageable);
        List<Vote> agendasAndVotes = voteRepository.findVoteWithAgendaByIds(agendaIds);
        return AgendaPageDTO.of(agendasAndVotes);
    }

    @Transactional
    public Long create(Long memberId, AgendaCreateRequestDTO agendaCreateRequestDTO) {
        MemberAuth memberAuth = memberAuthRepository.findMemberAuthWithMemberByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 id 입니다."));
        MemberAuth.validateMemberIsAdmin(memberAuth);

        Agenda agenda = Agenda.builder()
                .member(memberAuth.getMember())
                .agendaSubject(agendaCreateRequestDTO.getAgendaSubject())
                .agendaContent(agendaCreateRequestDTO.getAgendaContent())
                .build();
        Vote vote = Vote.builder()
                .agenda(agenda)
                .voteGubun(agendaCreateRequestDTO.getVoteGubun())
                .remainAvailableVoteCount(setDefaultValueIfVoteCountIsNull(agendaCreateRequestDTO))
                .startAt(agendaCreateRequestDTO.getVoteStartAt())
                .closeAt(agendaCreateRequestDTO.getVoteCloseAt())
                .build();

        Long agendaId = agendaRepository.save(agenda).getAgendaId();
        voteRepository.save(vote);

        return agendaId;
    }

    private long setDefaultValueIfVoteCountIsNull(AgendaCreateRequestDTO agendaCreateRequestDTO) {
        return agendaCreateRequestDTO.getAvailableVoteCount() == null ? 0 : agendaCreateRequestDTO.getAvailableVoteCount();
    }

}
