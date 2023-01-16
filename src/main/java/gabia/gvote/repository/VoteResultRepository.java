package gabia.gvote.repository;

import gabia.gvote.dto.VoteAdminResponseDTO;
import gabia.gvote.dto.VoteNormalResponseDTO;
import gabia.gvote.entity.VoteResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteResultRepository extends JpaRepository<VoteResult, Long> {

    // TODO : exists 최적화
    @Query("select count(vr.voteResultId) > 0 from VoteResult vr where vr.vote.voteId =:referenceVoteId")
    boolean existsByReferenceVoteId(@Param("referenceVoteId") Long referenceVoteId);

    @Query("select new gabia.gvote.dto.VoteNormalResponseDTO(vr.vote.voteId, vr.yesCount, vr.noCount, vr.abstentionCount) " +
            "from VoteResult vr " +
            "where vr.vote.voteId =:referenceVoteId")
    VoteNormalResponseDTO findByReferenceVoteIdSimpleNormalDTO(@Param("referenceVoteId") Long referenceVoteId);

    @Query("select new gabia.gvote.dto.VoteAdminResponseDTO(vr.vote.voteId, vr.yesCount, vr.noCount, vr.abstentionCount, vr.votedMemberHistories) " +
            "from VoteResult vr " +
            "where vr.vote.voteId =:referenceVoteId")
    VoteAdminResponseDTO findByReferenceVoteIdSimpleAdminDTO(@Param("referenceVoteId") Long referenceVoteId);
}
