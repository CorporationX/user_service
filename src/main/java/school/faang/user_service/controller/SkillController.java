package school.faang.user_service.controller;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
public record SkillController(SkillService skillService) {
    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);
        return skillService.create(skillDto);
    }

    public List<SkillDto> getUserSkills(long userId) {
        validateId(userId);
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        validateId(userId);
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        validateId(skillId);
        validateId(userId);
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle() == null || skillDto.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill title cant be empty or null");
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new DataValidationException("Incorrect id!");
        }
    }
}
