package gabia.gvote.repository;

import gabia.gvote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("select v from Vote v join fetch v.agenda where v.agenda.agendaId in :agendaIds")
    List<Vote> findVoteWithAgendaByIds(@Param("agendaIds") List<Long> agendaIds);

    @Query("select v from Vote v where v.agenda.agendaId =:agendaId")
    Optional<Vote> findByAgendaId(@Param("agendaId") Long agendaId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Vote v where v.voteId =:voteId")
    Optional<Vote> findByIdLock(@Param("voteId") Long voteId);
}
