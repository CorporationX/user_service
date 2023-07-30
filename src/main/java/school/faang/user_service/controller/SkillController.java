package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/skill")
public class SkillController {

    private final SkillService skillService;

    @PostMapping()
    public SkillDto create(@RequestBody SkillDto skillDto) {
        validateSkill(skillDto);
        return skillService.create(skillDto);
    }

    @GetMapping("/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable Long userId) {
        validateId(userId);
        return skillService.getUserSkills(userId);
    }

    @PutMapping("/offered/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable Long userId) {
        validateId(userId);
        return skillService.getOfferedSkills(userId);
    }

    @GetMapping("/{skillId}/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable Long skillId, @PathVariable Long userId) {
        validateId(userId);
        validateId(skillId);
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateId(Long userId) {
        if (userId == null) {
            throw new DataValidationException("Некорректный id!!!");
        }
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Название не может быть пустым!!!");
        }
    }
}
