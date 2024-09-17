package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SkillController {
    private final SkillService skillService;

    SkillDto create(SkillDto skill) {
        return skillService.create(skill);
    }

    List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }
}
