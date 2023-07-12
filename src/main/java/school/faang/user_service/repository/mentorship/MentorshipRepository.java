package school.faang.user_service.repository.mentorship;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM users u
            JOIN mentorship m  ON u.id = m.mentee_id
            WHERE m.mentor_id = ?
            """)
    List<User> findMenteesByMentorId(long mentorId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM users u
            JOIN mentorship m  ON u.id = m.mentor_id
            WHERE m.mentee_id = ?
            """)
    List<User> findMentorsByUserId(long userId);
}
