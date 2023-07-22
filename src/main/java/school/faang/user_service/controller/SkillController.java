package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skill")
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skillDTO) {
        skillDTO.validateSkill();
        return skillService.create(skillDTO);
    }

    public List<SkillDto> getUserSkills(long userId, int page, int size) {
        return skillService.getUserSkills(userId, page, size);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}