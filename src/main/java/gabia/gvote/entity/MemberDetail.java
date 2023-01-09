package gabia.gvote.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class MemberDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberDetailId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_detail_reference_member_id", nullable = false)
    private Member member;

    @Column(unique = true, nullable = false)
    private String memberEmail;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    private String memberAvatarImageUrl;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
