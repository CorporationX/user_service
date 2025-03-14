package school.faang.user_service.repository.leaderboard;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.leaderboard.UserActivity;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    @Query("""
            SELECT u FROM UserActivity as u
            ORDER BY u.rating DESC
            """)
    List<UserActivity> getTopActive(Pageable pageable);
}
