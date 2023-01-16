package gabia.gvote.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.gvote.entity.Vote;
import gabia.gvote.entity.VoteHistory;
import gabia.gvote.entity.VoteHistoryActionGubun;
import gabia.gvote.entity.VoteResult;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
public class VoteAdminResponseDTO implements VoteResponseDTO {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private long voteId;
    private long yesCount;
    private long noCount;
    private long abstentionCount;

    private List<VoteResult.VoteMemberHistory> votes;


    public VoteAdminResponseDTO(long voteId, long yesCount, long noCount, long abstentionCount, String votes) {
        this.voteId = voteId;
        this.yesCount = yesCount;
        this.noCount = noCount;
        this.abstentionCount = abstentionCount;
        try {
            this.votes = objectMapper.readValue(votes, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("cached voteResult parse Exception");
        }
    }

    public static VoteResult toEntity(Vote vote, Map<VoteHistoryActionGubun, Long> statistics, List<VoteHistory> voteHistories) {
        return VoteResult.builder()
                .vote(vote)
                .yesCount(statistics.get(VoteHistoryActionGubun.YES) == null ? 0: statistics.get(VoteHistoryActionGubun.YES))
                .noCount(statistics.get(VoteHistoryActionGubun.NO) == null ? 0: statistics.get(VoteHistoryActionGubun.NO))
                .abstentionCount(statistics.get(VoteHistoryActionGubun.ABSTENTION) == null ? 0: statistics.get(VoteHistoryActionGubun.ABSTENTION))
                .votedMemberHistories(voteHistories.stream()
                        .map(vh -> new VoteResult.VoteMemberHistory(vh.getMember().getMemberId(), vh.getVoteHistoryActionGubun(), vh.getVoteCount()))
                        .map(vh -> {
                            try {
                                return objectMapper.writeValueAsString(vh);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException("cached voteResult parse Exception");
                            }
                        })
                        .collect(Collectors.toList()).toString())
                .build();
    }

    public static VoteAdminResponseDTO of(VoteResult voteResult) {
        return new VoteAdminResponseDTO(voteResult.getVote().getVoteId(), voteResult.getYesCount(), voteResult.getNoCount(), voteResult.getAbstentionCount(), voteResult.getVotedMemberHistories());
    }
}
