package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {
    private final SkillService skillService;
    private final SkillValidator validator;

    @PostMapping("/add")
    public SkillDto create(@RequestBody SkillDto skillDto) {
        validator.validateSkill(skillDto.getTitle());
        return skillService.create(skillDto);
    }

    @GetMapping("/userSkills")
    public List<SkillDto> getUserSkills(@RequestParam("userId") long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/offeredSkills")
    public List<SkillCandidateDto> getOfferedSkills(@RequestParam("userId") long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PutMapping("/acquiredSkills")
    public SkillDto acquireSkillFromOffers(@RequestParam("skillId") long skillId, @RequestParam("userId") long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
