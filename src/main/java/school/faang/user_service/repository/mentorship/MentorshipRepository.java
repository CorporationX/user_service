package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.Optional;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT EXISTS (
                SELECT 1
                FROM mentorship
                WHERE mentor_id = :mentorId AND mentee_id = :menteeId
            )
            """)
    boolean findByMentorAndMentee(long mentorId, long menteeId);
}
