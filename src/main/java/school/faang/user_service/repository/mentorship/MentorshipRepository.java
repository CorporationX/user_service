package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;

import java.util.Optional;

@Repository
public interface MentorshipRepository extends JpaRepository<Mentorship, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO mentorship (mentor_id,mentee_id,created_at,updated_at)
            VALUES (?1,?2,NOW(),NOW())
            RETURNING *;
            """)
    Mentorship create(Long mentor_id, Long mentee_id);

    @Query(nativeQuery = true, value = """
            SELECT * FROM mentorship
            WHERE mentor_id = :mentorId AND mentee_id = :menteeId
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<Mentorship> getLastMentorship(Long mentorId, Long menteeId);
}
