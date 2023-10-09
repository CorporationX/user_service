package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/")
    public SkillDto create(@RequestBody SkillDto skill) {
        return skillService.create(skill);
    }

    @GetMapping("/{userId}/getUserSkill")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @GetMapping("/{skillId}/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    @GetMapping("/{skillId}")
    public SkillDto getSkillById(@PathVariable long skillId) {
        return skillService.getSkillById(skillId);
    }
}

