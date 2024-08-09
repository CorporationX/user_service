package school.faang.user_service.validator.requestvalidator;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.exception.RejectRecommendationException;
import school.faang.user_service.validator.Validator;

@Component
public class RejectRequestValidator  implements Validator<RecommendationRequest> {
    @Override
    public boolean validate(RecommendationRequest recommendation) {
        RequestStatus status = recommendation.getStatus();

        if (!status.equals(RequestStatus.PENDING)) {
            throw new RejectRecommendationException(
                    ExceptionMessage.IMPOSSIBLE_REJECTION,
                    recommendation.getStatus()
            );
        }

        return true;
    }
}
