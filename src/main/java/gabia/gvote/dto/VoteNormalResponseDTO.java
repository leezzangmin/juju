package gabia.gvote.dto;

import gabia.gvote.entity.VoteHistoryActionGubun;
import gabia.gvote.entity.VoteResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class VoteNormalResponseDTO implements VoteResponseDTO {

    private long voteId;
    private long yesCount;
    private long noCount;
    private long abstentionCount;

    public static VoteNormalResponseDTO of(VoteResult voteResult) {
        return VoteNormalResponseDTO.builder()
                .voteId(voteResult.getVote().getVoteId())
                .yesCount(voteResult.getYesCount())
                .noCount(voteResult.getNoCount())
                .abstentionCount(voteResult.getAbstentionCount())
                .build();
    }
}
