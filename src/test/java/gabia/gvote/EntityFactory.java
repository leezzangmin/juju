package gabia.gvote;

import gabia.gvote.entity.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class EntityFactory {

    public static Member generateMember() {
        LocalDateTime now = LocalDateTime.now();
        return Member.builder()
                .remainVoteCount(100L)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static MemberAuth generateAdminMemberAuth(Member member) {
        return MemberAuth.builder()
                .member(member)
                .memberGubun(MemberGubun.ADMIN)
                .memberStringId(UUID.randomUUID().toString())
                .memberStringPw(UUID.randomUUID().toString())
                .build();
    }

    public static MemberAuth generateNormalMemberAuth(Member member) {
        return MemberAuth.builder()
                .member(member)
                .memberGubun(MemberGubun.NORMAL)
                .memberStringId(UUID.randomUUID().toString())
                .memberStringPw(UUID.randomUUID().toString())
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

    public static Vote generateDoneUnlimitVote(Agenda agenda) {
        LocalDateTime startAt = LocalDateTime.of(2011, 01, 01, 01, 01, 01);
        LocalDateTime closeAt = LocalDateTime.of(2012, 01, 01, 01, 01, 01);
        LocalDateTime now = LocalDateTime.now();
        return Vote.builder()
                .agenda(agenda)
                .voteGubun(VoteGubun.UNLIMITED)
                .startAt(startAt)
                .closeAt(closeAt)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Vote generateBeforeUnlimitVote(Agenda agenda) {
        LocalDateTime startAt = LocalDateTime.of(2098, 01, 01, 01, 01, 01);
        LocalDateTime closeAt = LocalDateTime.of(2099, 01, 01, 01, 01, 01);
        LocalDateTime now = LocalDateTime.now();
        return Vote.builder()
                .agenda(agenda)
                .voteGubun(VoteGubun.UNLIMITED)
                .startAt(startAt)
                .closeAt(closeAt)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Vote generateInglimitVote(Agenda agenda) {
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime closeAt = LocalDateTime.now().plusDays(1);
        return Vote.builder()
                .agenda(agenda)
                .voteGubun(VoteGubun.LIMITED)
                .remainAvailableVoteCount(90L)
                .startAt(startAt)
                .closeAt(closeAt)
                .build();
    }

    public static Vote generateIngunlimitVote(Agenda agenda) {
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime closeAt = LocalDateTime.now().plusDays(1);
        return Vote.builder()
                .agenda(agenda)
                .voteGubun(VoteGubun.UNLIMITED)
                .startAt(startAt)
                .closeAt(closeAt)
                .build();
    }

    public static VoteHistory generate10YesVoteHistory(Vote vote, Member member) {
        LocalDateTime now = LocalDateTime.now();
        return VoteHistory.builder()
                .vote(vote)
                .member(member)
                .voteHistoryActionGubun(VoteHistoryActionGubun.YES)
                .voteCount(10L)
                .createdAt(now)
                .build();
    }

    public static VoteHistory generate10NoVoteHistory(Vote vote, Member member) {
        LocalDateTime now = LocalDateTime.now();
        return VoteHistory.builder()
                .vote(vote)
                .member(member)
                .voteHistoryActionGubun(VoteHistoryActionGubun.NO)
                .voteCount(10L)
                .createdAt(now)
                .build();
    }

    public static VoteHistory generate10AbstentionVoteHistory(Vote vote, Member member) {
        LocalDateTime now = LocalDateTime.now();
        return VoteHistory.builder()
                .vote(vote)
                .member(member)
                .voteHistoryActionGubun(VoteHistoryActionGubun.ABSTENTION)
                .voteCount(10L)
                .createdAt(now)
                .build();
    }
}
