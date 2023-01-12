package gabia.gvote.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class VoteHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_history_reference_vote_id", nullable = false)
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_history_reference_member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteHistoryActionGubun voteHistoryActionGubun;

    @Column(nullable = false)
    private Long voteCount;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

}
