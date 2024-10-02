package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@Valid @RequestBody SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/users/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable("userId") long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/users/{userId}/offered")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable("userId") long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/users/{userId}/acquiring/{skillId}")
    public SkillDto acquireSkillFromOffers(@PathVariable("skillId") long skillId, @PathVariable("userId") long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}