package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class RecommendationRequestValidator {

    private static final int MONTHS_BEFORE_NEXT_REQUEST = 6;

    public void validateRequestStatus(RecommendationRequest request) {
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
        return ChronoUnit.MONTHS.between(request.getCreatedAt(), LocalDateTime.now());
    }
}

