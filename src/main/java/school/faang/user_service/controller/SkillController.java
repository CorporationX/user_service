package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
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

    private void validateSkill(SkillDto skill) {
        if (Objects.isNull(skill)) {
            log.error("The SkillDto submitted in method validateSkill is null!");
            throw new DataValidationException("SkillDto from argument is null!");
        }
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            log.error("The SkillDto submitted to method validateSkill doesn't have a name!");
            throw new DataValidationException("SkillDto has no name!");
        }
    }
}
