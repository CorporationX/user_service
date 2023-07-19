package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.GoalInvitationService;

@Component
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    public void acceptGoalInvitation(long id) {
        validateId(id);
        goalInvitationService.acceptGoalInvitation(id);
    }

    private void validateId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Invalid request. Id can't be less than 0.");
        }
    }
}
