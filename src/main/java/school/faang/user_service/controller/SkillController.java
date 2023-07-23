package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skill) {
        String title = skill.getTitle();
        if (title == null || title.isBlank()) {
            throw new DataValidationException("Skill can't be created with empty name");
        }
        if (title.length() > 64){
            throw new DataValidationException("Skill's title length can't be more than 64 symbols");
        }
    }
}