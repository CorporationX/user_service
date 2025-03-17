package school.faang.user_service.validator.goalvalidator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.GoalDataException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class GoalValidator {
    public void validateTitle(GoalDto goal) {
        if (goal.getTitle().isBlank()) {
            log.error("Title can not be null or empty.");
            throw new GoalDataException("Title can not be null or empty.");
        }
    }

    public void validateSkills(GoalDto goal) {
        if (Objects.isNull(goal.getSkillIds()) || goal.getSkillIds().isEmpty()) {
            log.error("Can not create goal without skills.");
            throw new GoalDataException("Can not create goal without skills.");
        }
    }

    public void validateSkillsExistInBase(List<Skill> goalSkills, List<Skill> existingSkills) {
        if (!new HashSet<>(existingSkills).containsAll(goalSkills)) {
            log.error("Goal skills must be already exist.");
            throw new GoalDataException("Goal skills must be already exist.");
        }
    }
}
