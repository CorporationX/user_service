package school.faang.user_service.controller.skill;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Skill", description = "Endpoint for managing skills")
public class SkillController {

    private final SkillService skillService;

    @Operation(summary = "create skills")
    @PostMapping("/skill")
    public SkillDto create(@RequestBody SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @Operation(summary = "get User skills")
    @GetMapping("/skills")
    public List<SkillDto> getUserSkills(@PathVariable("userId") Long userid) {
        return skillService.getUserSkills(userid);
    }

    @Operation(summary = "get Offered Skills")
    @GetMapping("/OfferedSkills")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable("userId") Long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @Operation(summary = "acquire Skill From Offers")
    @PutMapping("/acquireSkillFromOffers")
    public SkillDto acquireSkillFromOffers(@PathVariable("skillId") Long skillId, @RequestHeader("userId") Long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}