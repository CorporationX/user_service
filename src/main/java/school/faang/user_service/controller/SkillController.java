package school.faang.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
public record SkillController(SkillService skillService) {
    public SkillDto create(SkillDto skillDto) {
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
