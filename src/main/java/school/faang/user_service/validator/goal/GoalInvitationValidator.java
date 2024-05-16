package school.faang.user_service.validator.goal;

import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

public interface GoalInvitationValidator {

    void validateUserIds(GoalInvitationCreateDto goalInvitationDto);

    void validateMaxGoals(int currentGoals);

    void validateGoalAlreadyExistence(Goal goal, User user);
}
