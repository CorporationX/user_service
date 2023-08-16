package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private RecommendationRequestMapper recommendationRequestMapper;
    private RecommendationRequestRepository recommendationRequestRepository;

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        validateRejectionDto(rejection);

        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation with id: " + id + " does not exist"));

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        request.setUpdatedAt(LocalDateTime.now());

        return recommendationRequestMapper.toDto(request);
    }

    public static void validateRejectionDto(RejectionDto rejection) {
        if (rejection == null || rejection.getReason().isBlank()) {
            throw new IllegalArgumentException("RejectionDto cannot be null or empty");
        }
    }
}
