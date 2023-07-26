package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public static final String REQUEST_NOT_FOUND = "Recommendation request not found";
    public static final String REQUEST_IS_NOT_PENDING = "Recommendation request is not pending";

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        Optional<RecommendationRequest> optRequest = recommendationRequestRepository.findById(id);
        RecommendationRequest request = optRequest.orElseThrow(() -> new IllegalArgumentException(REQUEST_NOT_FOUND));
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException(REQUEST_IS_NOT_PENDING);
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(request));
    }
}