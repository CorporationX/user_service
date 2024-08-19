package school.faang.user_service.validator.requestvalidator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.validator.Validator;

@Component
public class MessageValidator implements Validator<RecommendationRequestDto> {

    @Override
    public boolean validate(final RecommendationRequestDto recommendationRequestDto) {
        String msg = recommendationRequestDto.getMessage();

        boolean isBlankMsg = msg == null || msg.isBlank() || msg.isEmpty();

        if (isBlankMsg) {
            throw new ValidationException(
                ExceptionMessage.BLANK_RECOMMENDATION_MESSAGE
            );
        }

        return true;
    }
}
