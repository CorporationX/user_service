package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;

    public void validateRecommendationRequest(RecommendationRequestDto dto) {
        if (dto.getMessage() == null || dto.getMessage().isEmpty()){
            throw new IllegalArgumentException("Empty recommendation request!");
        }

        List<Long> skills = dto.getSkillId();
        if (skills == null || skills.isEmpty()){
            throw new IllegalArgumentException("Recommendation request cannot be without any skills!");
        }
    }

    public boolean hasPending(long requesterId, long receiverId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        return recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId)
                .map(recommendationRequest -> recommendationRequest.getCreatedAt().isAfter(sixMonthsAgo))
                .orElse(false);
    }
}
