package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserContext userContext;

    @Value("${recommendation.months.limit}")
    private int maxMonths;

    @Override
    public RecommendationRequestDto create(CreateRecommendationRequestDto recommendationDto) {
        long requesterId = userContext.getUserId();
        Long receiverId = recommendationDto.receiverId();
        validateRecommendationRequestRights(requesterId, receiverId);
        log.info("Creating new recommendation request");
        RecommendationRequest recommendationRequest = recommendationRequestMapper
                .toRecommendationRequest(recommendationDto);
        recommendationRequest.setRequester(userRepository.getByIdOrThrow(requesterId));
        recommendationRequest.setReceiver(userRepository.getByIdOrThrow(receiverId));
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequestRepository.save(recommendationRequest);
        return recommendationRequestMapper.toRecommendationRequestDto(recommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filters) {
        log.info("Performing search for submitted recommendation requests using filter(s)");

        List<RecommendationRequest> recommendationRequests =
                recommendationRequestRepository.getByFilters(filters);

        if (recommendationRequests.isEmpty()) {
            throw new EntityNotFoundException(
                    "No recommendation requests that match provided filters have been found");
        }

        return recommendationRequests.stream()
                .map(recommendationRequestMapper::toRecommendationRequestDto)
                .toList();
    }

    @Override
    public RecommendationRequestDto getById(long id) {
        return recommendationRequestMapper
                .toRecommendationRequestDto(recommendationRequestRepository.getByIdOrThrow(id));
    }

    @Override
    public void accept(long id) {
        RecommendationRequest recommendationRequest
                = validateRecommendationRequestDeclineOrAcceptRights(id);
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        recommendationRequestRepository.save(recommendationRequest);
        log.info("Recommendation request has been accepted");
    }

    @Override
    public void reject(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest
                = validateRecommendationRequestDeclineOrAcceptRights(id);
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.reason());
        recommendationRequestRepository.save(recommendationRequest);
        log.info("Recommendation request has been rejected");
    }

    private void validateRecommendationRequestRights(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new ForbiddenException("Requesting a recommendation from self is not allowed!");
        }
        RecommendationRequest latestRecommendationRequest = recommendationRequestRepository
                .findLatestPendingRequest(requesterId, receiverId).orElse(null);
        LocalDateTime maxPreviousRecommendationRequestDate = LocalDateTime.now().minusMonths(maxMonths);
        if (latestRecommendationRequest != null) {
            if (latestRecommendationRequest.getCreatedAt()
                    .isAfter(maxPreviousRecommendationRequestDate)
            ) {
                throw new ForbiddenException("Last recommendation request for this user"
                        + " was created later than 6 months ago");
            }
        }
    }

    private RecommendationRequest
        validateRecommendationRequestDeclineOrAcceptRights(long recommendationRequestId) {
        Long userId = userContext.getUserId();
        RecommendationRequest recommendationRequest = recommendationRequestRepository
                .findById(recommendationRequestId)
                .orElseThrow(() -> new DataValidationException(
                        "Recommendation request with this ID does not exist!"));
        if (!recommendationRequest.getReceiver().getId().equals(userId)) {
            throw new ForbiddenException("ID mismatch,"
                    + " accepting/declining this recommendation request is not allowed for this user!");
        }
        if (!recommendationRequest.getStatus().equals(RequestStatus.PENDING)) {
            throw new ForbiddenException("Only PENDING recommendation requests can be accepted/declined.");
        }
        return recommendationRequest;
    }
}
