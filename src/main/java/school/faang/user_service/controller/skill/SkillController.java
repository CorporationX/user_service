package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public SkillDto create(SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping
    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping
    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}