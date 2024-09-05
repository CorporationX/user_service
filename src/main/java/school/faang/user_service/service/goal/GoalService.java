package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_AMOUNT_ACTIVE_GOAL = 3;

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    public void createGoal(Long userId, GoalDto goal) {
        validateUserActiveGoalCount(userId);
        validateGoalSkillsIsExist(goal);
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent());
        addSkillsToGoal(goal);
    }

    public void updateGoal(Long userId, GoalDto goal) {
        validateGoalStatus(userId, goal);
        validateGoalSkillsIsExist(goal);
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            assignSkillsForUsers(goal);
        } else {
            updateSkillsInGoal(goal);
        }
    }

    private void updateSkillsInGoal(GoalDto goal) {
        goalRepository.removeSkillsFromGoal(goal.getId());
        goal.getSkillIds()
                .forEach(skillId -> goalRepository.addSkillToGoal(skillId, goal.getId()));
    }

    private void assignSkillsForUsers(GoalDto goal) {
        List<User> users = goalRepository.findUsersByGoalId(goal.getId());
        List<Long> skillIds = goal.getSkillIds();
        users.forEach(user -> assignSkillsForOneUser(user, skillIds));
    }

    private void assignSkillsForOneUser(User user, List<Long> skillIds) {
        skillIds.forEach(skillId -> skillRepository.assignSkillToUser(skillId, user.getId()));
    }

    private void validateUserActiveGoalCount(Long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_AMOUNT_ACTIVE_GOAL) {
            throw new IllegalArgumentException("The number of active user goals has been exceeded");
        }
    }

    private void validateGoalSkillsIsExist(GoalDto goal) {
        List<Long> skillIds = goal.getSkillIds();
        skillIds.forEach(skillId -> {
            if (!skillRepository.existsById(skillId)) {
                throw new IllegalArgumentException("Skill does not exist");
            }
        });
    }

    private void addSkillsToGoal(GoalDto goal) {
        goal.getSkillIds()
                .forEach(skillId -> goalRepository.addSkillToGoal(skillId, goal.getId()));
    }

    private void validateGoalStatus(Long userId, GoalDto goalDto) {
        Goal goalEntity = goalRepository.findGoalsByUserId(userId)
                .filter(goal -> goal.getId().equals(goalDto.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The goal does not exist"));

        if (goalEntity.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot update a completed goal");
        }
    }
}
