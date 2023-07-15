package school.faang.user_service.repository.mentorship;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {
    List<User> getAllByMentorId(long mentorId);

    List<User> getAllByMenteeId(long mentorId);
}
