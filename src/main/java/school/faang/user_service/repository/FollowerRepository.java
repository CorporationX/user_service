package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.dto.entity.User;

public interface FollowerRepository extends JpaRepository<User, Long> {
}
