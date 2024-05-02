package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.TooManyGoalsException;
import school.faang.user_service.service.skill.SkillService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    private final SkillService skillService;

    public void validateThatIdIsGreaterThan0(long id) {
        if (id < 1) {
            throw new DataValidationException("Id cannot be less than 1");
        }
    }

    public void validateGoalCreation(User user, Goal goal, int MAX_GOALS_AMOUNT) {
        if (user.getGoals().size() >= MAX_GOALS_AMOUNT) {
            throw new TooManyGoalsException("User has reached the maximum number of goals");}

        if (!allSkillsExist(goal.getSkillsToAchieve())) {
            throw new EntityNotFoundException("One or more skills required for the goal do not exist");}
    }

    public void validateGoalUpdate(Goal goal) {
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException("Goal is already completed");
        }

        if (allSkillsExist(goal.getSkillsToAchieve())) {
            throw new DataValidationException("One or more skills in the goal do not exist");
        }
    }

    private boolean allSkillsExist(List<Skill> skills) {
        return !skills.stream().allMatch(skill -> skillService.checkActiveSkill(skill.getId()));
    }

    public void validateFindSubtasks(List<Goal> subtasks, long goalId) {
        if (subtasks.isEmpty()) {
            throw new EntityNotFoundException("No subtasks found for goal with id: " + goalId);
        }
    }

    public List<GoalDto> validateGoalsByUserIdAndSort(List<GoalDto> goals, GoalFilterDto goalFilterDto) {
        return goals.stream()
                .filter(goal -> goalFilterDto.getTitle() == null || goal.getTitle().equals(goalFilterDto.getTitle()))
                .filter(goal -> goalFilterDto.getGoalStatus() == null || goal.getStatus() == goalFilterDto.getGoalStatus())
                .sorted(Comparator.comparing(GoalDto::getId))
                .collect(Collectors.toList());
    }
}
