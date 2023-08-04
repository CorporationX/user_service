package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(@Valid SkillDto skill) {
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}

