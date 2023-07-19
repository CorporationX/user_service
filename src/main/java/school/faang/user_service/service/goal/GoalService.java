package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
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

    public void updateGoal(Long goalId, Goal goal) {
        updateGoalValidation(goalId, goal);

        GoalDto dto = goalMapper.toDto(goal);

        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            List<Long> skillIds = dto.getSkillIds();
            goalRepository.findUsersByGoalId(goalId).forEach(user -> {
                skillIds.forEach(id -> skillRepository.assignSkillToUser(id, user.getId()));
            });
            goalRepository.deleteById(goalId);
        } else {
            goalRepository.save(goal);
        }

        goalRepository.update(goal.getTitle(), goal.getDescription(), goal.getStatus(), goalId);
        dto.getSkillIds().stream().forEach(sid -> goalRepository.updateGoalSkill(sid, goalId));
    }

    public void updateGoalValidation(Long goalId, Goal goal) {
        GoalDto dto = goalMapper.toDto(goal);
        GoalDto old = goalMapper.toDto(goalRepository.findGoal(goalId));
        if (dto.getStatus().equals(GoalStatus.COMPLETED) && old.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new IllegalArgumentException("Goal already completed");
        }

        if (skillRepository.countExisting(dto.getSkillIds()) != dto.getSkillIds().size()) {
            throw new IllegalArgumentException("Goal contains non-existent skill");
        }
    }

}
