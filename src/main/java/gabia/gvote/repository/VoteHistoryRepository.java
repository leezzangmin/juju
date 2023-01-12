package gabia.gvote.repository;

import gabia.gvote.entity.VoteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteHistoryRepository extends JpaRepository<VoteHistory, Long> {

    @Query("select vh from VoteHistory vh where vh.vote.voteId =:referenceVoteId")
    List<VoteHistory> findAllByReferenceVoteId(@Param("referenceVoteId") Long referenceVoteId);

}
