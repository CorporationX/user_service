package school.faang.user_service.repository.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.leaderboard.UserPopularity;

public interface UserPopularityRepository extends JpaRepository<UserPopularity, Long> {

}
