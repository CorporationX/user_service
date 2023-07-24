package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final RecommendationRequestRepository recommendationRepository;

    private final UserRepository userRepository;

    public void validationRequestDate(RecommendationRequestDto recommendationRequestDto) {
        LocalDateTime dateNowMinusSixMonths = LocalDateTime.now().minusMonths(6);
        LocalDateTime createdAt = recommendationRequestDto.getCreatedAt();
        if (createdAt.isAfter(dateNowMinusSixMonths)) {
            throw new DataValidationException(
                    "A recommendation request from the same user to another can be sent no more than once every 6 months");
        }
    }

    public void validationExistById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException("User with id " + id + " does not exist");
        }
    }
    private boolean hasPending(long requesterId, long receiverId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        return recommendationRepository.findLatestPendingRequest(requesterId, receiverId)
                .map(recommendationRequest -> recommendationRequest.getCreatedAt().isAfter(sixMonthsAgo))
                .orElse(false);
    }
}
