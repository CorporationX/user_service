package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping(value = "/skill", produces = MediaType.APPLICATION_JSON_VALUE)
public class SkillController {

    private final SkillService skillService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public SkillDto create(@RequestBody SkillDto skillDto) {
        validateSkill(skillDto);
        return skillService.create(skillDto);
    }

    @GetMapping("/{userId}")
    public List<SkillDto> getUserSkills(@PathVariable Long userId) {
        validateUserId(userId);
        return skillService.getUserSkills(userId);
    }


    @GetMapping("/offered/{userId}")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable Long userId) {
        validateUserId(userId);
        return skillService.getOfferedSkills(userId);
    }

    @GetMapping("/{skillId}/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable Long skillId, @PathVariable Long userId) {
        validateUserId(userId);
        validateSkillId(skillId);
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new DataValidationException("Некорректный id пользователя!!!");
        }
    }

    private void validateSkillId(Long skillId) {
        if (skillId == null) {
            throw new DataValidationException("Некорректный id скилла!!!");
        }
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isEmpty()) {
            throw new DataValidationException("Название не может быть пустым!!!");
        }
    }
}
