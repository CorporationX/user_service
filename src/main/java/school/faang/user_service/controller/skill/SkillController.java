package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/skill")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create (@RequestBody SkillDto skill) {
        validateSkill(skill.getTitle());

        return skillService.create(skill);
    }

    @GetMapping("/skill/{userId}")
    public List<SkillDto> getUserSkills (@PathVariable long userId) {
        return skillService.getUserSkills(userId);
    }

    @GetMapping("/skill/{userId}/offered")
    public List<SkillCandidateDto> getOfferedSkills (@PathVariable long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/skill/{userId}/offered/{skillId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill (String skillTitle) {
        if (skillTitle == null || skillTitle.isBlank()) {
            throw new DataValidationException("Invalid skill name.");
        }
    }
}
