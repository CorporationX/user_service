package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.User;

import java.util.List;

public interface MentorshipRepository extends CrudRepository<User, Long> {
    @Query(nativeQuery = true, value = """
        SELECT u.* FROM users u
        JOIN mentorship m ON u.id = m.mentee_id
        WHERE m.mentor_id = ?1
        """)
    List<User> findMenteesByMentorId(Long mentorId);

    @Query(nativeQuery = true, value = """
        SELECT u.* FROM users u
        JOIN mentorship m ON u.id = m.mentor_id
        WHERE m.mentee_id = ?1
        """)
    List<User> findMentorsByMenteeId(Long menteeId);

    @Query(nativeQuery = true, value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
        FROM mentorship
        WHERE mentor_id = ?1 AND mentee_id = ?2
        """)
    boolean existsByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM mentorship WHERE mentor_id = ?1 AND mentee_id = ?2
        """)
    void deleteByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM mentorship WHERE mentee_id = ?1 AND mentor_id = ?2
        """)
    void deleteByMenteeIdAndMentorId(Long menteeId, Long mentorId);
}
