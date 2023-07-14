package school.faang.user_service.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Controller
@Data
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    public List<SkillDto> getUserSkills (long userId){
        return skillService.getUserSkills (userId);
    }

    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        return skillService.create(skill);
    }

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("The title is not valid");
        }
    }
}
