package school.faang.user_service.repository.mentorship;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import school.faang.user_service.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {
    @Query("SELECT u FROM User u JOIN u.mentors m WHERE m.id = :mentorId")
    List<User> findAllMenteesByMentorId(@Param("mentorId") Long mentorId);

    @Query("SELECT m FROM User m JOIN m.mentees u WHERE u.id = :menteeId")
    List<User> findAllMentorsByMenteeId(@Param("menteeId") Long menteeId);

    @Modifying
    @Query("DELETE FROM mentorship m WHERE m.mentor_id = :mentorId AND m.mentee_id = :menteeId")
    int deleteMenteeFromMentor(@Param("menteeId") Long menteeId, @Param("mentorId") Long mentorId);

    @Modifying
    @Query("DELETE FROM mentorship m WHERE m.mentor_id = :mentorId AND m.mentee_id = :menteeId")
    int deleteMentorFromMentee(@Param("menteeId") Long menteeId, @Param("mentorId") Long mentorId);
}
