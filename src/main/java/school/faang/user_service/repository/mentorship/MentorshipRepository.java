package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.mentors m WHERE m.id = :mentorId")
    List<User> findMenteesByMentorId(@org.springframework.data.repository.query.Param("mentorId") long mentorId);

    @Query("SELECT u FROM User u JOIN u.mentors m WHERE m.id = :mentorId AND u.id = :menteeId")
    User findMenteeByMentorIdAndMenteeId(@org.springframework.data.repository.query.Param("mentorId") long mentorId, @org.springframework.data.repository.query.Param("menteeId") long menteeId);

    @Query("SELECT u FROM User u JOIN u.mentees m WHERE m.id = :menteeId AND u.id = :mentorId")
    User findMentorByMenteeIdAndMentorId(@org.springframework.data.repository.query.Param("menteeId") long menteeId, @Param("mentorId") long mentorId);
}
