package gabia.gvote.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class MemberAuth {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberAuthId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_auth_reference_member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberGubun memberGubun;

    @Column(nullable = false, length = 100)
    private String memberStringId;
    @Column(nullable = false, length = 100)
    private String memberStringPw;

    public static void validateMemberIsAdmin(MemberAuth memberAuth) {
        if (!MemberGubun.isAdmin(memberAuth)) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
    }
}
