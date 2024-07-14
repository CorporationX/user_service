package school.faang.service.user.controller;

import org.springframework.stereotype.Component;
import school.faang.service.user.dto.skill.SkillCandidateDto;
import school.faang.service.user.dto.skill.SkillDto;
import school.faang.service.user.exception.DataValidationException;
import school.faang.service.user.service.SkillService;

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
        validateId(skillId, userId);
        return skillService.acquireSkillFromOffers(skillId, userId);
    }

    private void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle() == null || skillDto.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Skill title cant be empty or null");
        }
    }

    private void validateId(Long... ids) {
        if (ids == null) {
            throw new DataValidationException("Id cant be null");
        }
        for (Long id : ids) {
            if (id == null) {
                throw new DataValidationException("Id cant be null");
            }
        }
    }
}
