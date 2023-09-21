package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    @Value("${spring.goals.user_max_count}")
    private int userMaxGoalsCount;

    public void validateToCreate(Long userId, GoalDto goalDto) {
        long userSkillCount = goalRepository.countActiveGoalsPerUser(userId);
        if (userSkillCount >= userMaxGoalsCount) {
            throw new DataValidationException("User can't have more than " + userMaxGoalsCount + " active goals");
        }
        checkSkills(goalDto);
    }

    public void validateToUpdate(Goal goal, GoalDto goalDto) {
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new DataValidationException("You cannot update completed goal");
        }
        checkSkills(goalDto);
    }

    private void checkSkills(GoalDto goalDto) {
        if (Objects.nonNull(goalDto.getSkillIds())) {
            goalDto.getSkillIds().forEach(skillId -> {
                if (!skillRepository.existsById(skillId)) {
                    throw new DataValidationException("Skill with id " + skillId + " does not exist");
                }
            });
        }
    }
}
