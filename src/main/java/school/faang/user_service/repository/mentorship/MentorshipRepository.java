package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;

public interface MentorshipRepository extends JpaRepository<User, Long> {

    default User getByIdOrThrow(long userId) {
        return findById(userId)
                .orElseThrow(() -> EntityNotFoundException.of(String.format("User %d not found", userId)));
    }

    @Query(nativeQuery = true, value = """
            SELECT EXISTS (SELECT 1 FROM mentorship m WHERE m.mentor_id = :mentorId AND m.mentee_id IN (:menteeIds))
            """
    )
    boolean existsByMentorIdAndMenteeIds(Long mentorId, List<Long> menteeIds);
}
