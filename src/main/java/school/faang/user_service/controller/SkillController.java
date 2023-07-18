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



    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("The title is not valid");
        }
    }
    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId){
        return skillService.getOfferedSkills(userId);
    }
}
