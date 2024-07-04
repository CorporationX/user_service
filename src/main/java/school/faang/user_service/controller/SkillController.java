package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }
}
