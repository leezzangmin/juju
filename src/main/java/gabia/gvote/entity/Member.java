package gabia.gvote.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private Long remainVoteCount;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void decreaseVoteCount(Long voteCount) {
        validateVoteCount(voteCount);
        this.remainVoteCount -= voteCount;
    }

    private void validateVoteCount(Long voteCount) {
        if (this.getRemainVoteCount() < voteCount) {
            throw new IllegalStateException("사용하려는 의결권이 보유한 의결권보다 적습니다.");
        }
    }
}
