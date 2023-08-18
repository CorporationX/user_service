package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Tag(name = "Управление скиллами")
@RestController
@RequestMapping("api/v1/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @Operation(summary = "Добавить скилл")
    @PostMapping("/")
    public SkillDto create(SkillDto skill) {
        return skillService.create(skill);
    }

    @Operation(summary = "Получить скиллы пользователя по идентификатору")
    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @Operation(summary = "Получить предложенные пользователю скиллы")
    @GetMapping("/offered")
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @Operation(summary = "Приобрести предложенные пользователю скиллы")
    @PostMapping("/acquire/{skillId}/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
