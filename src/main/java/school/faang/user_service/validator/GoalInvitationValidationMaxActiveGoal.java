package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidationMaxActiveGoal {

    private final UserRepository userRepository;
    private final int MAXIMUM_NUMBER_OF_ACTIVE_TARGETS = 3;

    public boolean isCheckActiveTargetUser(GoalInvitation invitation){
        User invitedUser = invitation.getInvited();
        return false;
    }

    private int countActiveGoal(User user) {
       return 0;
    }

}
