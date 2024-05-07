package school.faang.user_service.validator;

import org.springframework.util.CollectionUtils;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;

public class RecommendationRequestValidator {
    public static void validateForCreate(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getRequesterId() == null) {
            throw new DataValidationException("Recommendation request requester ID is empty");
        }
        if (recommendationRequest.getReceiverId() == null) {
            throw new DataValidationException("Recommendation request receiver ID is empty");
        }
        if (CollectionUtils.isEmpty(recommendationRequest.getSkills())) {
            throw new DataValidationException("Recommendation request skill requests are empty");
        }
    }
}
