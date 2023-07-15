package school.faang.user_service.repository.mentorship;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {

    Optional<User> findUserById(Long id);

    @Query(nativeQuery = true, value = """
            DELETE FROM mentorship m
            WHERE m.mentor_id = ?1 AND m.mentee_id = ?2
            """)
    void deleteMentee(long mentorId, long menteeId);
}
