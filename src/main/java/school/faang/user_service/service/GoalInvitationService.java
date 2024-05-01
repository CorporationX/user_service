package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

@Component
public class GoalInvitationService {
    private GoalInvitationRepository goalInvitationRepository;

    public GoalInvitationService(GoalInvitationRepository goalInvitationRepository) {
        this.goalInvitationRepository = goalInvitationRepository;
    }
    void createInvitation() {

    }
}
