package school.faang.user_service.repository.mentorship;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.event.Mentorship;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;

public interface MentorshipRepository extends JpaRepository<User, Long> {

    default User getByIdOrThrow(long userId) {
        return findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %d not found", userId)));
    }

    @Query(nativeQuery = true, value = """
            SELECT * FROM mentorship WHERE mentor_id = ?1 AND mentee_id = ?2
            """)
    List<Mentorship> findMasterAndPadavanIds(long mentorId, long menteeId);

    @Query(nativeQuery = true, value = """
            INSERT INTO mentorship (mentor_id, mentee_id, created_at, updated_at)
            VALUES (?1, ?2, NOW(), NOW())
            returning *
            """)
    Mentorship createMentorship(long mentorId, long menteeId);
}
