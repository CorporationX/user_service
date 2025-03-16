package school.faang.user_service.repository.leaderboard;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.leaderboard.UserPopularity;

import java.util.List;

public interface UserPopularityRepository extends JpaRepository<UserPopularity, Long> {
    @Query("""
            SELECT u FROM UserPopularity as u
            ORDER BY u.impact DESC
            """)
    List<UserPopularity> getTopPopular(Pageable pageable);
}
