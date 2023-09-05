package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.UserRepository;


import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidationMaxActiveGoal {

    private final UserRepository userRepository;
    private final int MAXIMUM_NUMBER_OF_ACTIVE_TARGETS = 3;

    public void isCheckActiveTargetUser(User invitedUSer){
      int maxGoal = countActiveGoal(invitedUSer.getGoals().stream());
      if (maxGoal < MAXIMUM_NUMBER_OF_ACTIVE_TARGETS) {
          throw new IllegalArgumentException("The user already has the maximum number of goals");
      }
    }



    private int countActiveGoal(Stream<Goal> userStream) {
        return userStream
                .filter(goal -> goal.getStatus().name().equals("ACTIVE"))
                .toList().size() + 1;

    }

}
