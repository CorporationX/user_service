package school.faang.user_service.repository.score;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.score.UserScore;

import java.util.Optional;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(nativeQuery = true, value = """
            SELECT user_score
            FROM user_score
            WHERE id = :userId
            """)
    Optional<UserScore> findForUpdate(@Param("userId") long userId);
}
