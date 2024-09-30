package school.faang.user_service.controller.skill;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.skill.SkillCandidateDto;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.groups.CreateGroup;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/skills")
@Validated
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@NotNull @Validated(CreateGroup.class) @RequestBody SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/users/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable("userId") @Positive long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/users/{userId}/offered")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable("userId") @Positive long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/users/{userId}/acquiring/{skillId}")
    public SkillDto acquireSkillFromOffers(@PathVariable("skillId") @Positive long skillId, @PathVariable("userId") @Positive long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}