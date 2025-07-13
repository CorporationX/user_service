package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.EntityNotFoundException;

public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {

    default GoalInvitation getByIdOrThrow(long invitationId) {
        return findById(invitationId)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Goal invitation %d not found", invitationId))
                );
    }
}