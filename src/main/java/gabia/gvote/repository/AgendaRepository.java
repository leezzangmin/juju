package gabia.gvote.repository;

import gabia.gvote.entity.Agenda;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    @Query("select a.agendaId from Agenda a")
    List<Long> findIdsByPage(Pageable pageable);

}
