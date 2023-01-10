package gabia.gvote.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class IdPwDTO {

    @NotBlank
    private String memberStringId;
    @NotBlank
    private String memberPw;
}
