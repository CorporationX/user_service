package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.rejection.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final  RecommendationRequestMapper recommendationRequestMapper;
    public RecommendationRequestDto getRequest(long userId) {
        RecommendationRequest foundPerson = recommendationRequestRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalStateException(String.format("There is no person in the row with id %d", userId));
        });
        return recommendationRequestMapper.toDto(foundPerson);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        validateRejectionDto(rejection);
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("Recommendation with id %d doesn't exist", id))
                );

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());

        return recommendationRequestMapper.toDto(request);
    }

    private static void validateRejectionDto(RejectionDto rejection) {
        if (rejection == null || rejection.getReason().isBlank()) {
            throw new IllegalArgumentException("Rejection and its reason mustn't be null or empty.");
        }
    }
}