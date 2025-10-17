package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SkillController {
    private final SkillService skillService;
    private final UserContext userContext;

    SkillDto create(CreateSkillDto skillDto) {
        if (skillDto.getTitle() == null || skillDto.getTitle().isBlank()) {
            throw new DataValidationException("skill title should not be empty");
        }
        return skillService.create(skillDto);
    }

    List<SkillDto> getByUserId(Long userId) {
        if (userId == null) {
            throw new DataValidationException("userId should not be empty");
        }
        return skillService.getByUserId(userId);
    }

    List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }

    void acquireSkillFromOffers(long skillId) {
        skillService.acquireSkillFromOffers(skillId, userContext.getUserId());
    }
}
