package gabia.gvote.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_reference_agenda_id", nullable = false)
    private Agenda agenda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteGubun voteGubun;

    @ColumnDefault(value = "0")
    private Long remainAvailableVoteCount;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime closeAt;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public VoteStatus calculateCurrentVoteStatus() {
        LocalDateTime current = LocalDateTime.now();
        return VoteStatus.calculate(current, this.getStartAt(), this.getCloseAt());
    }

    public void closeVote() {
        LocalDateTime current = LocalDateTime.now();
        if (closeAt.isBefore(current)) {
            throw new IllegalArgumentException("이미 종료된 투표입니다.");
        }
        this.closeAt = current;
    }

    public void minusAvailableVoteCount(Long voteCount) {
        validateLimitedVoteCount(voteCount);
        this.remainAvailableVoteCount -= voteCount;
    }

    private void validateLimitedVoteCount(Long voteCount) {
        if (this.getRemainAvailableVoteCount() < voteCount) {
            throw new IllegalStateException("의결권 제한된 남은 숫자가 투표하려는 숫자보다 작습니다.");
        }
    }
}
