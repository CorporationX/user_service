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

    public void isRecommendationRequestMessageNull(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest.getMessage() != null && !recommendationRequest.getMessage().isBlank()) {
            return;
        }
        log.error("Error! Invalid request message!");
        throw new RecommendationRequestNotValidException("Recommendation request message text can't be null or empty!");
    }

    public void validateRequesterAndReceiverExists(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestRepository.findById(recommendationRequestDto.getRequesterId()).isEmpty() ||
                recommendationRequestRepository.findById(recommendationRequestDto.getReceiverId()).isEmpty()) {
            log.error("User doesn't exist in DB!");
            throw new RecommendationRequestNotValidException("Either requester or receiver doesn't exist!");
        }
    }

    public RecommendationRequest validateRecommendationRequestExists(long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(() ->
                new RecommendationRequestNotValidException("No such recommendation request exist!"));
    }

    public void validatePreviousRequest(RecommendationRequestDto recommendationRequestDto) {
        LocalDateTime presentMoment = LocalDateTime.now();
        RecommendationRequest last = recommendationRequestRepository.findLatestPendingRequest(
                        recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId())
                .orElseThrow();
        LocalDateTime lastRequest = last.getCreatedAt();
        long monthsBetween = ChronoUnit.MONTHS.between(lastRequest, presentMoment);
        if (monthsBetween < 6) {
            log.error("Months between last request is : {}", monthsBetween);
            throw new RecommendationRequestNotValidException("It has not been six months since the last request!");
        }
    }

    public RecommendationRequest validateRequestStatusNotAcceptedOrDeclined(long id) {
        RecommendationRequest rq =
                validateRecommendationRequestExists(id);
        if (rq.getStatus() != RequestStatus.PENDING) {
            throw new RecommendationRequestNotValidException("Request already was accepted or rejected!");
        }
        return rq;
    }
}
