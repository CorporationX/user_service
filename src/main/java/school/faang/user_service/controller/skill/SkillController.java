package school.faang.user_service.controller.skill;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
@Tag(name = "Skills", description = "Endpoints for managing skills")
public class SkillController {

    private final SkillService skillService;
    private final UserContext userContext;

    @Operation(summary = "Create a skill")
    @PostMapping
    public SkillDto create(@Valid @RequestBody SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @Operation(summary = "Get user's skills by user id")
    @GetMapping
    public List<SkillDto> getUserSkills() {
        return skillService.getUserSkills(userContext.getUserId());
    }

    @Operation(summary = "Get offered skills by user id")
    @GetMapping("/offered")
    public List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }

    @Operation(summary = "Acquire skill from offers")
    @PutMapping("/{skillId}/offered")
    public SkillDto acquireSkillFromOffers(@PathVariable @Positive(message = "ID can't be less than 1") long skillId) {
        return skillService.acquireSkillFromOffers(skillId, userContext.getUserId());
    }
}