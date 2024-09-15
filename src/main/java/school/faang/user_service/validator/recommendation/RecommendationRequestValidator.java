package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.recomendation.RecommendationRequestNotValidException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestValidator {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private static final int MONTHS_BEFORE_NEXT_REQUEST = 6;
    private static final String REQUESTER = "Requester doesn't exist!";
    private static final String RECEIVER = "Receiver doesn't exist!";
    private static final String REQUESTER_AND_RECEIVER = "Requester and receiver don't exist!";

    public void validateRecommendationRequestMessageNotNull(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() == null || recommendationRequest.getMessage().isBlank()) {
            log.error("Error! Invalid request message!");
            throw new RecommendationRequestNotValidException("Recommendation request message text can't be null or empty!");
        }
    }

    public void validateRequesterAndReceiverExists(RecommendationRequestDto recommendationRequestDto) {
        boolean requester = recommendationRequestRepository.findById(recommendationRequestDto
                .getRequesterId()).isPresent();
        boolean receiver = recommendationRequestRepository.findById(recommendationRequestDto
                .getReceiverId()).isPresent();

        if (!requester && !receiver) {
            log.error(REQUESTER_AND_RECEIVER);
            throw new RecommendationRequestNotValidException(REQUESTER_AND_RECEIVER);
        } else if (!requester) {
            log.error(REQUESTER);
            throw new RecommendationRequestNotValidException(REQUESTER);
        } else if (!receiver) {
            log.error(RECEIVER);
            throw new RecommendationRequestNotValidException(RECEIVER);
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

    private Long lastRequest(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest last = recommendationRequestRepository.findLatestPendingRequest(
                        recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId())
                .orElseThrow(() -> new RecommendationRequestNotValidException("No such recommendation exists!"));
        return ChronoUnit.MONTHS.between(last.getCreatedAt(), LocalDateTime.now());
    }
}

