package school.faang.user_service.repository.mentorship;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {

    @Query("SELECT m.mentee FROM Mentorship m WHERE m.mentor.id = :mentorId")
    List<User> findMenteesByMentorId(@Param("mentorId") long mentorId);

    @Query("SELECT m.mentor FROM Mentorship m WHERE m.mentee.id = :menteeId")
    List<User> findMentorsByMenteeId(@Param("menteeId") long menteeId);

    @Modifying
    @Query("DELETE FROM Mentorship m WHERE m.mentor.id = :mentorId AND m.mentee.id = :menteeId")
    void deleteMenteeOfMentor(@Param("mentorId") long mentorId, @Param("menteeId") long menteeId);

    @Modifying
    @Query("DELETE FROM Mentorship m WHERE m.mentor.id = :mentorId AND m.mentee.id = :menteeId")
    void deleteMentorOfMentee(@Param("mentorId") long mentorId, @Param("menteeId") long menteeId);
}
