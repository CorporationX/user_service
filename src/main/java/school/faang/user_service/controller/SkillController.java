package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        throwIfTrue(skill == null, "skill cannot be null");
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        throwIfTrue(skillId == null || userId == null, "skillId or userId cannot be null");
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void throwIfTrue(boolean condition, String errorMessage) {
        if (condition) {
            throw new DataValidationException(errorMessage);
        }
    }
}

