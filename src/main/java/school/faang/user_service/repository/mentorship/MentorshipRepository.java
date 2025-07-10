package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;

public interface MentorshipRepository extends JpaRepository<User, Long> {

    default User getByIdOrThrow(long userId) {
        return findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %d not found", userId)));
    }
}
