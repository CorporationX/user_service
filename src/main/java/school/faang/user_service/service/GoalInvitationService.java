package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalRepository goalRepository;

    public void rejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invalid request. Requested goal invitation not found"));

        if (goalRepository.existsById(goalInvitation.getGoal().getId())) {
            goalInvitation.setStatus(RequestStatus.REJECTED);
            goalInvitationRepository.save(goalInvitation);
        } else {
            throw new EntityNotFoundException("Invalid request. Requested goal not found");
        }
    }
}
