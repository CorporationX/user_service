package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private static final String MSG = "There is no recommendation with such id";

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest foundPerson = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    throw new IllegalStateException(MSG);
                });

        return recommendationRequestMapper.toDto(foundPerson);
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        validateRejectionDto(rejection);

        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation with id: " + id + " does not exist"));

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());

        return recommendationRequestMapper.toDto(request);
    }

    private static void validateRejectionDto(RejectionDto rejection) {
        if (rejection == null || rejection.getReason().isBlank()) {
            throw new IllegalArgumentException("Rejection and its reason must not be null or empty.");
        }
    }
}
