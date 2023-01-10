package gabia.gvote.service;

import gabia.gvote.dto.AgendaPageDTO;
import gabia.gvote.entity.Vote;
import gabia.gvote.repository.AgendaRepository;
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

    @Transactional(readOnly = true)
    public AgendaPageDTO agendaPagination(Pageable pageable) {

        List<Long> agendaIds = agendaRepository.findIdsByPage(pageable);

        List<Vote> agendasAndVotes = voteRepository.findVoteWithAgendaByIds(agendaIds);

        return AgendaPageDTO.of(agendasAndVotes);
    }


}
