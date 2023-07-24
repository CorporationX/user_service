package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Component
@RequiredArgsConstructor
public class SkillValidator {

    private final SkillRepository skillRepository;

    public void validationExistSkill(RecommendationRequestDto recommendationRequestDto) {
        for (Long skillRequestId : recommendationRequestDto.getSkillId()) {
            if (!skillRepository.existsById(skillRequestId)) {
                throw new DataValidationException("Skill with id " + skillRequestId + " does not exist");
            }
        }
    }
}
