package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;

import static school.faang.user_service.exception.recommendation.RecommendationRequestExceptions.*;

@Component
public class RecommendationRequestValidator {
    public void validateForCreate(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getRequesterId() == null) {
            throw new DataValidationException(REQUEST_REQUESTER_ID_EMPTY.getMessage());
        }
        if (recommendationRequest.getReceiverId() == null) {
            throw new DataValidationException(REQUEST_RECEIVER_ID_EMPTY.getMessage());
        }
        if (CollectionUtils.isEmpty(recommendationRequest.getSkills())) {
            throw new DataValidationException(REQUEST_SKILLS_EMPTY.getMessage());
        }
    }
}
