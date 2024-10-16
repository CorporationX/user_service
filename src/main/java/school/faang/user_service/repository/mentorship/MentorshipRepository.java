package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.entity.User;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {
    @Modifying(clearAutomatically = true)
    @Query(value= """
            DELETE FROM mentorship
            WHERE mentee_id = :menteeId AND mentor_id = :mentorId
            """, nativeQuery = true)
    void deleteMentorship(long menteeId, long mentorId);
}
