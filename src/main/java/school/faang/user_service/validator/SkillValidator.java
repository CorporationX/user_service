package school.faang.user_service.validator;

import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null) throw new NullPointerException("skill name is null");
        if (skill.getTitle().isEmpty() || skill.getTitle().isBlank())
            throw new DataValidationException("skill name is either blank or empty");
        else if (skillRepository.existsByTitle(skill.getTitle()))
            throw new DataValidationException("skill already exists");
    }

    public void validateUserSkills(List<Skill> skills) {
        if (skills.isEmpty()) throw new DataValidationException("the list of skills is empty");
    }
}
