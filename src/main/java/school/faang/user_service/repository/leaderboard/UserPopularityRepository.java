package school.faang.user_service.repository.leaderboard;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.leaderboard.UserImpact;

import java.util.List;

public interface UserPopularityRepository extends JpaRepository<UserImpact, Long> {
    @Query("""
            SELECT u FROM UserImpact as u
            ORDER BY u.rating DESC
            """)
    List<UserImpact> getTopPopular(Pageable pageable);
}
