package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/skill")
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

    @GetMapping("/{skillId}")
    public SkillDto getSkillById(@PathVariable Long skillId) {
        return skillService.getSkillById(skillId);
    }

    private void throwIfTrue(boolean condition, String errorMessage) {
        if (condition) {
            throw new DataValidationException(errorMessage);
        }
    }
}

