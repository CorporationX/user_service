package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exсeption.DataValidationException;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exсeption.EntityNotFoundException;
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

    public GoalDto updateGoal(long id, GoalDto goalDto) {
        updateGoalValidation(id, goalDto);

        Goal goal = goalMapper.toEntity(goalDto);

        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            List<Long> skillIds = goalDto.getSkillIds();
            goalRepository.findUsersByGoalId(id).forEach(user -> {
                skillIds.forEach(skillId -> skillRepository.assignSkillToUser(skillId, user.getId()));
            });
            goalRepository.deleteById(id);
        } else {
            return goalMapper.toDto(goalRepository.save(goal));
        }

        return null;
    }

    public void updateGoalValidation(long id, GoalDto goalDto) {
        Goal oldg = goalRepository.findGoal(id);
        if (oldg.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException("Goal already completed!");
        }

        List<Long> skillIds = goalDto.getSkillIds();
        if (skillRepository.countExisting(skillIds) != skillIds.size()) {
            throw new EntityNotFoundException("Goal contains non-existent skill!");
        }
    }

}
