package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateAllSkillsIdsExist(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(ids);
            if (skills.size() != ids.size()) {
                throw new DataValidationException("There are no skills with those ids");
            }
        }
    }

    public void validateSkill(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill \"" + skill.getTitle() + "\" already exist");
        }
    }

    public void checkUserSkill(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User with id:" + userId + " already have skill with id" + skillId);
        }
    }

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
