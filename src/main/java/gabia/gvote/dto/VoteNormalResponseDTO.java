package gabia.gvote.dto;

import gabia.gvote.entity.VoteHistoryActionGubun;
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
    private long AbstentionCount;

    public static VoteNormalResponseDTO of(Long voteId, Map<VoteHistoryActionGubun, Long> statistics) {
        return VoteNormalResponseDTO.builder()
                .voteId(voteId)
                .yesCount(statistics.get(VoteHistoryActionGubun.YES))
                .noCount(statistics.get(VoteHistoryActionGubun.NO))
                .AbstentionCount(statistics.get(VoteHistoryActionGubun.ABSTENTION))
                .build();
    }
}
