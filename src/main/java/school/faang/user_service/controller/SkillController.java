package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public SkillDto create(SkillDto skillDto) {
        if (!validateSkill(skillDto)) {
            throw new DataValidationException("Нет названия навыка");
        }
        return skillService.create(skillDto);
    }

    public boolean validateSkill(SkillDto skillDto) {
        boolean valid = true;
        if (skillDto.getTitle() == null || skillDto.getTitle().isBlank()) {
            valid = false;
        }
        return valid;
    }

    public void getUserSkills(Long userId) {
        skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId);
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}
