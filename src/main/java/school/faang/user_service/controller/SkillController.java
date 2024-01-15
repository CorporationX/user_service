package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.skill.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle().isBlank()) {
            throw new DataValidationException("Введите наименование навыка");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        validateNullUserId(userId);
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        validateNullUserId(userId);
        return skillService.getOfferedSkills(userId);
    }

    private void validateNullUserId(long userId) {
        if (userId == 0) {
            throw new DataValidationException("Id пользователя не задано");
        }
    }
}