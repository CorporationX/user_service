package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skill")
public class SkillController {
    private final SkillService skillService;
    private final SkillValidator skillValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create (@RequestBody SkillDto skill) {
        skillValidator.validateSkill(skill.getTitle());

        return skillService.create(skill);
    }

    @GetMapping("/{userId}")
    public List<SkillDto> getUserSkills (@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/{userId}/offered")
    public List<SkillCandidateDto> getOfferedSkills (@PathVariable long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/{userId}/offered/{skillId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
