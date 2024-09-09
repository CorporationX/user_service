package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.User;
import java.util.List;

public interface MentorshipRepository extends JpaRepository<User, Long> {
    List<User> findMenteesByMentorId(long mentorId);
    User findMenteeByMentorIdAndMenteeId(long mentorId, long menteeId);
    User findMentorByMenteeIdAndMentorId(long menteeId, long mentorId);
}