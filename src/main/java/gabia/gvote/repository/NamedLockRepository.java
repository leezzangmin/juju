package gabia.gvote.repository;

import gabia.gvote.entity.VoteResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NamedLockRepository extends JpaRepository<VoteResult, Long> {
    @Query(value = "select get_lock(:key, 10000)", nativeQuery = true)
    void getNamedLock(@Param("key") String key);
    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseNamedLock(@Param("key") String key);
}
