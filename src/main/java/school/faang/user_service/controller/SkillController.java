package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);

        return skillService.create(skill);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null) {
            throw new DataValidationException("Skill title is required");
        }

        if (skill.getTitle().isBlank()) {
            throw new DataValidationException("Skill with id " + skill.getId() + " is blank");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffer(long skillId, long userId) {
        return skillService.acquireSkillFromOffer(skillId, userId);
    }
}
