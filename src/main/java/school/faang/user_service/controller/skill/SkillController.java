package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@Valid @RequestBody CreateSkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/user/{userId}")
    public List<SkillDto> getByUserId(@PathVariable @NotNull @Positive Long userId) {
        return skillService.getByUserId(userId);
    }

    @GetMapping("/offered")
    public List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }

    @PostMapping("/{skillId}/acquire")
    public void acquireSkillFromOffers(@PathVariable @NotNull @Positive Long skillId) {
        skillService.acquireSkillFromOffers(skillId);
    }
}
