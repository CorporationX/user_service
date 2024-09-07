package school.faang.user_service.service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MentorshipRepository extends JpaRepository<Mentee, Long> {
    List<Mentee> findMenteesByUserId(long userId);
}