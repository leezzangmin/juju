package gabia.gvote.dto;

import gabia.gvote.entity.VoteGubun;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AgendaCreateRequestDTO {

    @NotBlank
    private String agendaSubject;
    @NotBlank
    private String agendaContent;
    @NotNull
    private LocalDateTime voteStartAt;
    @NotNull
    private LocalDateTime voteCloseAt;
    @NotNull
    private VoteGubun voteGubun;
    private Long availableVoteCount;

}
