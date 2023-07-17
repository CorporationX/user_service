package school.faang.user_service.repository.mentorship;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.Optional;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {
    Optional<User> findUserById(long id);
}
