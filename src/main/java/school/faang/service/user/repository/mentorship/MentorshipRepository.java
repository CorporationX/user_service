package school.faang.service.user.repository.mentorship;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.service.user.entity.User;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {
}
