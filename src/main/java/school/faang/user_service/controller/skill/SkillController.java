package school.faang.user_service.controller.skill;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Skill", description = "Endpoint for managing skills")
public class SkillController {

    private final SkillService skillService;
    private final UserContext userContext;
    @Operation(summary = "create skills")
    @PostMapping("/skill")
    public SkillDto create(@RequestBody SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @Operation(summary = "Get user's skills by user id")
    @GetMapping("/skills")
    public List<SkillDto> getUserSkills() {
        return skillService.getUserSkills(userContext.getUserId());
    }

    @Operation(summary = "get Offered Skills")
    @GetMapping("/offered_skills")
    public List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }

    @Operation(summary = "acquire Skill From Offers")
    @PutMapping("/skill/{skillId}/offered_skills")
    public SkillDto acquireSkillFromOffers(@PathVariable Long skillId) {
        return skillService.acquireSkillFromOffers(skillId, userContext.getUserId());
    }
}