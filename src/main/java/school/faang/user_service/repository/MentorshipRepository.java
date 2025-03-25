package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorshipRepository extends CrudRepository<Mentorship, Long> {
    boolean existsByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    @Query("SELECT m.id FROM Mentorship m WHERE m.mentor.id = :mentorId AND m.mentee.id = :menteeId")
    Optional<Long> findIdByMentorIdAndMenteeId(long mentorId, long menteeId);

    @Query("SELECT m.mentee FROM Mentorship m WHERE m.mentor.id = :mentorId")
    List<User> findAllMenteesByMentorId(long mentorId);

    @Query("SELECT m.mentor FROM Mentorship m WHERE m.mentee.id = :menteeId")
    List<User> findAllMentorsByMenteeId(long menteeId);
}