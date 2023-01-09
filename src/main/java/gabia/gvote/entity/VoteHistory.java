package gabia.gvote.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Enumerated
    @Column(nullable = false)
    private VoteHistoryActionGubun voteHistoryActionGubun;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

}
