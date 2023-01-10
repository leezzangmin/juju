package gabia.gvote.repository;

import gabia.gvote.entity.MemberAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberAuthRepository extends JpaRepository<MemberAuth, Long> {

    // TODO: string_id 인덱스 생성 필요
    @Query("select ma from MemberAuth ma where ma.memberStringId =:stringId")
    Optional<MemberAuth> findByStringId(@Param("stringId") String stringId);
}
