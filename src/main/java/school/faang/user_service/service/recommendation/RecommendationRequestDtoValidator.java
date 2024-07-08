package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class RecommendationRequestDtoValidator {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;

    public void validateRecommendationRequestDto(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto.getMessage().isEmpty()) {
            throw new IllegalArgumentException("recommendation message can't be empty");
        }

        if (recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()).isEmpty()) {
            throw new EntityNotFoundException("requesterId and recieverId must be in the database");
        }

        LocalDateTime currentRequestTime = recommendationRequestDto.getCreatedAt();
        LocalDateTime latestRequestTime = recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId()).get().getCreatedAt();

        if (ChronoUnit.MONTHS.between(currentRequestTime, latestRequestTime) < 6) {
            throw new IllegalArgumentException("Sorry, but you can create recommendation request only once every 6 months.\n" +
                    "Your latest recommendation request create time: " + latestRequestTime);
        }

        if (!recommendationRequestDto.getSkills().stream()
                .map(requestSkill -> requestSkill.getSkill().getTitle()).allMatch(skillRepository::existsByTitle)) {
            throw new IllegalArgumentException("Not all requested skill exists");
        }
    }
}
