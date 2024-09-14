package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.filter.RequestFilterDtoException;
import school.faang.user_service.exception.recomendation.RecommendationRequestNotValidException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestValidator {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private static final int MONTHS_BEFORE_NEXT_REQUEST = 6;

    public void validateRecommendationRequestMessageNotNull(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            log.error("Error! Invalid request message!");
            throw new RecommendationRequestNotValidException("Recommendation request message text can't be null or empty!");
        }
    }

    public void validateRequesterAndReceiverExists(RecommendationRequestDto recommendationRequestDto) {
        boolean requesterExists = recommendationRequestRepository
                .findById(recommendationRequestDto.getRequesterId()).isPresent();
        boolean receiverExists = recommendationRequestRepository
                .findById(recommendationRequestDto.getReceiverId()).isPresent();
        if (!requesterExists && !receiverExists) {
            log.error("Requester and receiver don't exist in DB!");
            throw new RecommendationRequestNotValidException("Requester and receiver don't exist!");
        } else if (!requesterExists) {
            log.error("Requester doesn't exist in DB!");
            throw new RecommendationRequestNotValidException("Receiver doesn't exist!");
        } else if (!receiverExists) {
            log.error("Receiver doesn't exist in DB!");
            throw new RecommendationRequestNotValidException("Requester doesn't exist!");
        }
    }

    public RecommendationRequest validateRecommendationRequestExists(long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(() ->
                new RecommendationRequestNotValidException("No such recommendation request exist!"));
    }

    public void validatePreviousRequest(RecommendationRequestDto recommendationRequestDto) {
        if (lastRequest(recommendationRequestDto) < MONTHS_BEFORE_NEXT_REQUEST) {
            log.error("Months between last request is : {}", lastRequest(recommendationRequestDto));
            throw new RecommendationRequestNotValidException("It should take 6 months from the date of submission!");
        }
    }

    public RecommendationRequest validateRequestStatusNotAcceptedOrDeclined(long id) {
        RecommendationRequest rq = validateRecommendationRequestExists(id);
        if (rq.getStatus() != RequestStatus.PENDING) {
            throw new RecommendationRequestNotValidException("Request already was accepted or rejected!");
        }
        return rq;
    }

    public void validateRequestDtoFilterFieldsNotNull(RequestFilterDto filter) {
        if (filter == null) {
            log.error("Passing null parameter!");
            throw new RequestFilterDtoException("Filter is null!");
        }
        Field[] fields = filter.getClass().getDeclaredFields();
        boolean allFieldsNull = Arrays.stream(fields)
                .allMatch(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(filter) == null;
                    } catch (IllegalAccessException e) {
                        return true;
                    }
                });
        if (allFieldsNull) {
            log.error("All fields null");
            throw new RequestFilterDtoException("All fields can't be null!");
        }
    }

    private Long lastRequest(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest last = recommendationRequestRepository.findLatestPendingRequest(
                        recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId())
                .orElseThrow(() -> new RecommendationRequestNotValidException("No such recommendation exists!"));
        return ChronoUnit.MONTHS.between(last.getCreatedAt(), LocalDateTime.now());
    }
}

