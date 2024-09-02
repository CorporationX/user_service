package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;
    public void validateSkill(SkillDto skill){
        if(skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("title can't be null");
        } else if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("the skill already exists");
        }
    }
}
