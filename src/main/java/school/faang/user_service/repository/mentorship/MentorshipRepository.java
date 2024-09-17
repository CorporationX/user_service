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

    @Query("SELECT u.mentees FROM User u WHERE u.id = :mentorId")
    List<User> findMenteesByMentorId(@Param("mentorId") long mentorId);

    @Query("SELECT u.mentors FROM User u WHERE u.id = :menteeId")
    List<User> findMentorsByMenteeId(@Param("menteeId") long menteeId);
}
