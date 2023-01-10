package gabia.gvote;

import gabia.gvote.entity.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class EntityFactory {

    public static Member generateNormalMember() {
        LocalDateTime now = LocalDateTime.now();
        return Member.builder()
                .remainVoteCount(100L)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Agenda generateAgenda(Member member) {
        LocalDateTime now = LocalDateTime.now();
        return Agenda.builder()
                .member(member)
                .agendaSubject(UUID.randomUUID().toString())
                .agendaContent(UUID.randomUUID().toString())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Vote generateBeforeUnlimitVote(Agenda agenda, Member member) {

        LocalDateTime startAt = LocalDateTime.of(2098, 01, 01, 01, 01, 01);
        LocalDateTime closeAt = LocalDateTime.of(2099, 01, 01, 01, 01, 01);
        LocalDateTime now = LocalDateTime.now();

        return Vote.builder()
                .agenda(agenda)
                .remainAvailableVoteCount(10000L)
                .voteStatus(VoteStatus.BEFORE)
                .voteGubun(VoteGubun.UNLIMITED)
                .startAt(startAt)
                .closeAt(closeAt)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
