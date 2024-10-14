package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/skill")
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    public SkillDto create(@Valid @RequestBody SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/user/{id}")
    public List<SkillDto> getUserSkills(@PathVariable long id) {
        return skillService.getUserSkills(id);
    }

    @GetMapping("/user/offered/{id}")
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PutMapping
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
