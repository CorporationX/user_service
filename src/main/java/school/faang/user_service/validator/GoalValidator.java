package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    public static final int MAX_USER_GOALS_SIZE = 3;

    private final GoalRepository goalRepository;

    public void validateCreate(GoalDto goalDto, User user) {
        validateUser(user);

        validateTitle(goalDto);
        validateDescription(goalDto);
    }

    public void validateUpdate(Goal goal, GoalDto goalDto) {
        validateGoalOnUpdate(goal);

        validateTitle(goalDto);
        validateDescription(goalDto);
    }

    public void validateUser(User user) {
        if (user.getGoals().size() > MAX_USER_GOALS_SIZE - 1) {
            throw new IllegalArgumentException("Number of goals should be no more than " + MAX_USER_GOALS_SIZE);
        }
    }

    public void validateGoalOnUpdate(Goal goal) {
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Goal with id " + goal.getId() + " is already completed. Completed goals cannot be changed.");
        }
    }

    public void validateTitle(GoalDto goalDto) {
        boolean isTitleNotUnique = goalRepository.existsGoalByTitle(goalDto.getTitle());
        if (isTitleNotUnique) {
            throw new IllegalArgumentException("Goal with title \"%s\" already exist".formatted(goalDto.getTitle()));
        }
    }

    public void validateDescription(GoalDto goalDto) {
        boolean isDescriptionNotUnique = goalRepository.existsGoalByDescription(goalDto.getDescription());
        if (isDescriptionNotUnique) {
            throw new IllegalArgumentException("Goal with description \"%s\" already exist".formatted(goalDto.getDescription()));
        }
    }
}
