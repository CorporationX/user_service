package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("${domain.path}/skills")
@RequiredArgsConstructor
@Slf4j
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@Valid @RequestBody SkillDto skill) {
        return skillService.create(skill);
    }

    @GetMapping("/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/offered")
    public List<SkillCandidateDto> getOfferedSkills(@RequestParam long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/acquire")
    public SkillDto acquireSkillFromOffers(@RequestParam long skillId, @RequestParam long userId) {
        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);
        log.info("Successfully acquired skill: {}", result);
        return result;
    }
}
