package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skill")
public class SkillController {
    private final SkillService service;

    @PostMapping("/create")
    public SkillDto create(@RequestBody SkillDto skillDto) {
        validateSkill(skillDto);
        return service.save(skillDto);
    }

    @GetMapping("/{userId}/skills")
    List<SkillDto> getUserSkills(@PathVariable long userId) {
        return service.getUserSkills(userId);
    }

    @GetMapping("/{userId}/offered-skills")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        return service.getOfferedSkills(userId);
    }
    @PostMapping("/acquire/{skillId}/user/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return service.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill must have a non-empty title.");
        }
    }
}
