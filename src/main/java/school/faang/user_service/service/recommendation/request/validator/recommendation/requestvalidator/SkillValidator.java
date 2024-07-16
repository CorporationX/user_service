package school.faang.user_service.service.recommendation.request.validator.recommendation.requestvalidator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.recommendation.request.validator.Validator;

import java.util.List;

@Component
@AllArgsConstructor
public class SkillValidator implements Validator<RecommendationRequestDto> {

    private final SkillRepository skillRep;

    @Override
    public boolean validate(final RecommendationRequestDto recommendationRequestDto) {
        List<Long> skillIds = recommendationRequestDto.getSkillIds();
        boolean areSkillsExists = skillRep.countExisting(skillIds) == skillIds.size();

        if (! areSkillsExists) {
            throw new ValidationException(
                    ExceptionMessage.SKILLS_DONT_EXIST
            );
        }

        return areSkillsExists;
    }
}
