package gabia.gvote.dto;

import gabia.gvote.entity.MemberGubun;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SessionMemberAuthDTO {

    private Long memberId;
    private MemberGubun memberGubun;
}
