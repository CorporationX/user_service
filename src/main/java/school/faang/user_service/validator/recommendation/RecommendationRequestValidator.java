package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.recomendation.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestValidator {
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestService recommendationRequestService;
    private static final int MONTHS_BEFORE_NEXT_REQUEST = 6;

    private void validateRecommendationRequestMessageNotNull(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            log.error("Error! Invalid request message!");
            throw new DataValidationException("Recommendation request message text can't be null or empty!");
        }
    }

    public void validateRequestStatusNotAcceptedOrDeclined(RecommendationRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Request already was accepted or rejected!");
        }
    }

    public void validatePreviousRequest(RecommendationRequest request) {
        Long monthsFromLastRequest = getMonthsFromLastRequest(request);
        if (monthsFromLastRequest < MONTHS_BEFORE_NEXT_REQUEST) {
            log.error("Months between last request is : {}", monthsFromLastRequest);
            throw new DataValidationException("It should take " +
                    MONTHS_BEFORE_NEXT_REQUEST + " months from the date of submission!");
        }
    }

    private Long getMonthsFromLastRequest(RecommendationRequest request) {
        RecommendationRequest lastRequest = recommendationRequestService.getLastPendingRequest(request);
        return ChronoUnit.MONTHS.between(lastRequest.getCreatedAt(), LocalDateTime.now());
    }

    public void validateRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        validateRecommendationRequestMessageNotNull(recommendationRequestDto);
        validatePreviousRequest(recommendationRequestMapper.toEntity(recommendationRequestDto));
    }
}

