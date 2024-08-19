package school.faang.user_service.validator.requestvalidator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.Validator;

import java.time.LocalDateTime;
import java.time.Period;

@Component
public class CooldownValidator implements Validator<RecommendationRequestDto> {

    @Qualifier("cooldownOfRequestRecommendation")
    private final Period cooldownPeriod;
    private final RecommendationRequestRepository recommendationRequestRep;

    CooldownValidator(
            RecommendationRequestRepository recommendationRequestRepository,
            @Qualifier("cooldownOfRequestRecommendation") Period cooldownPeriod
    ) {
        recommendationRequestRep = recommendationRequestRepository;
        this.cooldownPeriod = cooldownPeriod;
    }

    @Override
    public boolean validate(final RecommendationRequestDto recommendationRequestDto) {
        Long requesterId = recommendationRequestDto.getRequesterId();
        Long receiverId = recommendationRequestDto.getReceiverId();

        var lastRequest =
                recommendationRequestRep.findLatestRecommendationRequestFromRequesterToReceiver(
                        requesterId, receiverId
                );

        if (lastRequest.isEmpty()) {
            return true;
        }

        LocalDateTime dateOfLastRequest = lastRequest.get().getCreatedAt();
        LocalDateTime dateOfCurrentRequest = recommendationRequestDto.getCreateAt();

        if (!isExceededCooldown(dateOfCurrentRequest, dateOfLastRequest)) {
            throw new ValidationException(
                ExceptionMessage.RECOMMENDATION_COOLDOWN_NOT_EXCEEDED,
                    cooldownPeriod.toString()
            );
        }

        return true;
    }

    private boolean isExceededCooldown(LocalDateTime newRequestDate, LocalDateTime oldRequestDate) {
        LocalDateTime nextAllowedRequestDate = oldRequestDate.plus(cooldownPeriod);
        return newRequestDate.isAfter(nextAllowedRequestDate);
    }
}
