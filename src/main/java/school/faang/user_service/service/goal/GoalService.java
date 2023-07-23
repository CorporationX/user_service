package school.faang.user_service.service.goal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exeptions.EntityNotFoundException;
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

    public void updateGoal(Long goalId, GoalDto goal) {
        updateGoalValidation(goalId, goal);

        Goal newGoal = goalMapper.toEntity(goal);

        if (newGoal.getStatus().equals(GoalStatus.COMPLETED)) {
            List<Long> skillIds = goal.getSkillIds();
            goalRepository.findUsersByGoalId(goalId).forEach(user -> {
                skillIds.forEach(id -> skillRepository.assignSkillToUser(id, user.getId()));
            });
            goalRepository.deleteById(goalId);
        } else {
            goalRepository.save(newGoal);
        }
    }

    public void updateGoalValidation(Long goalId, GoalDto goal) {
        Goal oldg = goalRepository.findGoal(goalId);
        if (oldg.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException("Goal already completed");
        }

        if (skillRepository.countExisting(goal.getSkillIds()) != goal.getSkillIds().size()) {
            throw new EntityNotFoundException("Goal contains non-existent skill");
        }
    }

}
