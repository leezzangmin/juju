package gabia.gvote.dto;

import gabia.gvote.entity.Vote;
import gabia.gvote.entity.VoteStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class AgendaPageDTO {

    private List<SingleAgenda> agendas;
    private int count;

    @Getter
    @AllArgsConstructor
    public static class SingleAgenda {
        private final Long agendaId;
        private final String agendaSubject;

        private final Long voteId;
        private final VoteStatus voteStatus;
        private final LocalDateTime voteStartAt;
    }

    public static AgendaPageDTO of(List<Vote> agendasAndVotes) {
        return new AgendaPageDTO(agendasAndVotes.stream()
                .map(i -> new SingleAgenda(i.getAgenda().getAgendaId(),
                        i.getAgenda().getAgendaSubject(),
                        i.getVoteId(),
                        i.getVoteStatus(),
                        i.getStartAt()))
                .collect(Collectors.toList()), agendasAndVotes.size());

    }

}
