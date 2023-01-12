package gabia.gvote.dto;

import gabia.gvote.entity.VoteHistoryActionGubun;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteCreateRequestDTO {

    @Min(value = 1)
    private long voteCount;

    @NotNull
    private VoteHistoryActionGubun voteHistoryActionGubun;

}
