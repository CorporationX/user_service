package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        validateData(skill);
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        validateData(userId);
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        validateData(userId);
        return skillService.getOfferedSkills(userId);
    }

    private void validateData(long userId) {
        if (userId < 0) {
            throw new DataValidationException("userId cannot be negative");
        }
    }

    private void validateData(SkillDto skill) {
        if (skill == null || skill.getTitle() == null) {
            throw new DataValidationException("skill cannot be empty");
        }
    }
}