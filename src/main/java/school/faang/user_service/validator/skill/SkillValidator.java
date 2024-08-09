package school.faang.user_service.validator.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.Validator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator implements Validator<RecommendationRequestDto> {
    private final SkillRepository skillRepository;

    public void validateSkillDto(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("skill has no name");
        } else if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("skill already exists");
        }
    }

    @Override
    public boolean validate(final RecommendationRequestDto recommendationRequestDto) {
        List<Long> skillIds = recommendationRequestDto.getSkillIds();
        boolean areSkillsExists = skillRepository.countExisting(skillIds) == skillIds.size();

        if (! areSkillsExists) {
            throw new ValidationException(
                    ExceptionMessage.SKILLS_DONT_EXIST
            );
        }

        return areSkillsExists;
    }

}
