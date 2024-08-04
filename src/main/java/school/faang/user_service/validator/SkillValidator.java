package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateSkillDto(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("skill has no name");
        } else if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("skill already exists");
        }
    }

}
