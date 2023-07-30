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
import school.faang.user_service.validator.GoalValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator validator;



    public GoalDto updateGoal(long id, GoalDto goalDto) {
        validator.updateGoalServiceValidation(id, goalDto);

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
}
