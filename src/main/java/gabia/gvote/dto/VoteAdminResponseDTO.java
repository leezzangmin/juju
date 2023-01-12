package gabia.gvote.dto;

import gabia.gvote.entity.VoteHistory;
import gabia.gvote.entity.VoteHistoryActionGubun;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class VoteAdminResponseDTO implements VoteResponseDTO {

    private long voteId;
    private long yesCount;
    private long noCount;
    private long AbstentionCount;

    private List<VoteMemberHistory> votes;

    @Getter
    @AllArgsConstructor
    public static class VoteMemberHistory {
        private long memberId;
        private VoteHistoryActionGubun voteHistoryActionGubun;
        private long voteCount;
    }

    public static VoteAdminResponseDTO of(Long voteId, Map<VoteHistoryActionGubun, Long> statistics, List<VoteHistory> voteHistories) {
        return VoteAdminResponseDTO.builder()
                .voteId(voteId)
                .yesCount(statistics.get(VoteHistoryActionGubun.YES))
                .noCount(statistics.get(VoteHistoryActionGubun.NO))
                .AbstentionCount(statistics.get(VoteHistoryActionGubun.ABSTENTION))
                .votes(voteHistories.stream()
                        .map(vh -> new VoteMemberHistory(vh.getMember().getMemberId(), vh.getVoteHistoryActionGubun(), vh.getVoteCount()))
                        .collect(Collectors.toList()))
                .build();
    }

}
