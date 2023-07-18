package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final int MAX_ACTIVE_GOALS = 3;

    public void updateGoalValidation(Long goalId, GoalDto goal) {
        GoalDto old = goalMapper.toDto(goalRepository.findGoal(goalId));
        if (goal.getStatus().equals(GoalStatus.COMPLETED) && old.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new IllegalArgumentException("Goal already completed");
        }

        if (skillRepository.countExisting(goal.getSkillIds()) != goal.getSkillIds().size()) {
            throw new IllegalArgumentException("Goal contains non-existent skill");
        }
    }

    public void updateGoal(Long goalId, GoalDto goal) {
        updateGoalValidation(goalId, goal);
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            List<Long> skillIds = goal.getSkillIds();
            goalRepository.findUsersByGoalId(goalId).forEach(user -> {
                skillIds.forEach(id -> skillRepository.assignSkillToUser(id, user.getId()));
            });
        }

        goalRepository.update(goal.getTitle(), goal.getDescription(), goal.getStatus(), goalId);
        goal.getSkillIds().stream().forEach(sid -> goalRepository.updateGoalSkill(sid, goalId));
    }

}
