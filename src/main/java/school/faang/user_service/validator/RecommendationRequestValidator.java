package school.faang.user_service.validator;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Data
public class RecommendationRequestValidator {
    private static final int SIX_MONTHS_IN_DAYS = 30 * 6;

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;

    public void validateCreateRecommendationRequest(RecommendationRequest recommendationRequest, List<Long> skills) {
        Long requesterId = recommendationRequest.getRequester().getId();
        Long receiverId = recommendationRequest.getReceiver().getId();

        if (requesterId.equals(receiverId)) {
            throw new ValidationException("You cannot ask for a recommendation from yourself");
        }

        this.checkUserExists(requesterId);
        this.checkUserExists(receiverId);
        this.checkSkillsExists(skills);
        this.checkLastRequestMoreSixMonths(
            requesterId,
            receiverId
        );
    }

    private void checkSkillsExists(List<Long> skills) {
        skills.forEach((skillId) -> {
            if (!this.skillRepository.existsById(skillId)) {
                throw new ValidationException(String.format("Skill id=%s not found", skillId));
            }
        });
    }

    private void checkUserExists(Long userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new ValidationException("User not found");
        }
    }

    private void checkLastRequestMoreSixMonths(Long requesterId, Long receiverId) {
        Optional<RecommendationRequest> lastRecommendationRequest = this.recommendationRequestRepository
                .findLatestPendingRequest(
                    requesterId,
                    receiverId
                );

        if (lastRecommendationRequest.isPresent()) {
            Duration duration = Duration.between(
                lastRecommendationRequest.get().getCreatedAt(),
                LocalDateTime.now()
            );

            if (duration.toDays() < SIX_MONTHS_IN_DAYS) {
                throw new ValidationException("The last request was sent less than 6 months ago");
            }
        }
    }
}
