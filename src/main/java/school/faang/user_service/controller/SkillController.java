package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Validated
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(@Valid SkillDto skillDto) {
        if (skillDto.validateTitle()) {
            return skillService.create(skillDto);
        }
        throw new DataValidationException("skill title is invalid");
    }

    public List<SkillDto> getUserSkills(long id) {
        return skillService.getUserSkills(id);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
