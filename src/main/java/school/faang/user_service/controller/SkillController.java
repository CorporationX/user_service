package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Tag(name = "skill_methods")
@Controller
@RequestMapping("/skills")
@Slf4j
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @Operation(
            summary = "Создать навык",
            description = "Создает новый навык в системе и возвращает его DTO."
    )
    @PostMapping
    public SkillDto create(@RequestBody SkillDto skill) {
        log.info("Starting skill creation...");
        SkillDto createdSkill = skillService.create(skill);
        log.info("Skill creation completed!");
        return createdSkill;
    }

    @Operation(
            summary = "Получить навыки пользователя",
            description = "Возвращает список DTO навыков, которыми обладает указанный пользователь."
    )
    @GetMapping("/user/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        log.info("Getting skills for the user {}", userId);
        List<SkillDto> skills = skillService.getUserSkills(userId);
        log.info("Get {} skills for the user {}", skills.size(), userId);
        return skills;
    }

    @Operation(
            summary = "Получить предложенные навыки для пользователя",
            description = "Возвращает список DTO навыков, предложенных пользователю для приобретения."
    )
    @GetMapping("/offered/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        log.info("Getting offered skills for the user {}", userId);
        List<SkillCandidateDto> offeredSkills = skillService.getOfferedSkills(userId);
        log.info("Get {} offered skills for the user {}", offeredSkills.size(), userId);
        return offeredSkills;
    }

    @Operation(
            summary = "Приобрести предложенный навык",
            description = "Позволяет пользователю приобрести один из предложенных ему навыков."
    )
    @PutMapping("/acquire")
    public SkillDto acquireSkillFromOffers(@RequestParam long skillId, @RequestParam long userId) {
        log.info("Acquiring skill {} for the user {}", skillId, userId);
        SkillDto acquiredSkills = skillService.acquireSkillFromOffers(skillId, userId);
        log.info("Skill {} acquired for user {}", skillId, userId);
        return acquiredSkills;
    }
}
