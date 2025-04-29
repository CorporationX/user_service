package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillViewDto create(SkillCreateDto skill )  {
        return skillService.create(skill);


    }
    public List<SkillViewDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);

    }
    public List <SkillCandidateDto> getOfferedSkills(long userId) {
    return skillService.getOfferedSkills(userId);
    }
}

