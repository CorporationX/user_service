package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.recomendation.DataValidationException;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class SkillValidator {

    public void validateSkillsExist(List<Long> skillIds, List<Skill> skills) {
        if (skills.isEmpty() || skillIds.isEmpty()) {
            log.error("Skills or skill ids is empty!");
            throw new DataValidationException("Skills don't exist!");
        } else if (skills.size() != skillIds.size()) {
            log.error("Not all skills exist wanted {} , but found {}", skillIds.size(), skills.size());
            throw new DataValidationException("Not all skills exist!");
        }
    }
}
