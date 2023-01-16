package gabia.gvote.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString(exclude = "vote")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VoteResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteResultId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_vote_id", nullable = false)
    private Vote vote;

    @Column(nullable = false)
    private Long yesCount;

    @Column(nullable = false)
    private Long noCount;

    @Column(nullable = false)
    private Long abstentionCount;

    @Column(columnDefinition = "longtext")
    private String votedMemberHistories;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VoteMemberHistory {
        private long memberId;
        private VoteHistoryActionGubun voteHistoryActionGubun;
        private long voteCount;
    }

}
