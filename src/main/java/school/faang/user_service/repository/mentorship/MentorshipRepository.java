package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;

@Repository
public interface MentorshipRepository extends JpaRepository<Mentorship, Long> {
    @Query(nativeQuery = true, value = "SELECT EXISTS (SELECT 1 FROM mentorship WHERE mentor_id = :mentorId AND mentee_id = :menteeId)")
    boolean existsByMentorIdAndMenteeId(Long mentorId, Long menteeId);
}
