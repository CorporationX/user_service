package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

import school.faang.user_service.exception.DataValidationException;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/skill")
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@RequestBody SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    @GetMapping("/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/offered/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PutMapping("/acquire/{skillId}/user/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skill) {
        String title = skill.getTitle();
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Skill can't be created with empty name");
        }
        if (title.length() > 64) {
            throw new DataValidationException("Skill's title length can't be more than 64 symbols");
        }
    }
}