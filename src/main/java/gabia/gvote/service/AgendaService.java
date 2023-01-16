package gabia.gvote.service;

import gabia.gvote.dto.AgendaCreateRequestDTO;
import gabia.gvote.dto.AgendaPageDTO;
import gabia.gvote.dto.SessionMemberAuthDTO;
import gabia.gvote.entity.*;
import gabia.gvote.repository.AgendaRepository;
import gabia.gvote.repository.MemberAuthRepository;
import gabia.gvote.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public AgendaPageDTO agendaPagination(Pageable pageable) {
        List<Long> agendaIds = agendaRepository.findIdsByPage(pageable);
        List<Vote> agendasAndVotes = voteRepository.findVoteWithAgendaByIds(agendaIds);
        return AgendaPageDTO.of(agendasAndVotes);
    }

    @Transactional
    public Long create(SessionMemberAuthDTO sessionMemberAuthDTO, AgendaCreateRequestDTO agendaCreateRequestDTO) {
        Member member = memberRepository.findById(sessionMemberAuthDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id의 회원입니다."));

        Agenda agenda = Agenda.builder()
                .member(member)
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

    @Transactional
    public void delete(Long agendaId) {
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 안건 id 입니다."));
        Vote vote = voteRepository.findByAgendaId(agendaId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표 id 입니다."));
        validateVoteStatus(vote);

        voteRepository.delete(vote);
        agendaRepository.delete(agenda);
    }

    private static void validateVoteStatus(Vote vote) {
        if (!vote.calculateCurrentVoteStatus().equals(VoteStatus.BEFORE)) {
            throw new IllegalStateException("이미 시작되거나 끝난 투표는 종료할 수 없습니다.");
        }
    }

    private long setDefaultValueIfVoteCountIsNull(AgendaCreateRequestDTO agendaCreateRequestDTO) {
        return agendaCreateRequestDTO.getAvailableVoteCount() == null ? 0 : agendaCreateRequestDTO.getAvailableVoteCount();
    }

}
