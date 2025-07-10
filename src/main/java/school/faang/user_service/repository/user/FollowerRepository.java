package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.user.User;

public interface FollowerRepository extends JpaRepository<User, Long> {
}
