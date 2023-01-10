package gabia.gvote.repository;

import gabia.gvote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("select v from Vote v join fetch v.agenda where v.agenda.agendaId in :agendaIds")
    List<Vote> findVoteWithAgendaByIds(@Param("agendaIds") List<Long> agendaIds);

}
