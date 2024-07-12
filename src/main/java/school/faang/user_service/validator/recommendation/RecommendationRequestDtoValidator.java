package school.faang.user_service.validator.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationRequestDtoValidator {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;

    public void validateAll(RecommendationRequestDto recommendationRequestDto) {
        validateMessage(recommendationRequestDto.getMessage());
        validateRequesterAndReceiverIds(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());
        validateRequestTimeDifference(recommendationRequestDto.getCreatedAt(), recommendationRequestDto.getRequesterId(), recommendationRequestDto.getReceiverId());
        validateRequestedSkills(recommendationRequestDto.getSkills());
    }

    public void validateMessage(String message) {
        if (message.isEmpty()) {
            throw new IllegalArgumentException("recommendation message can't be empty");
        }
    }

    public void validateRequesterAndReceiverIds(Long requesterId, Long receiverId) {
        if (recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(receiverId, requesterId).isEmpty()) {
            throw new EntityNotFoundException("requesterId and recieverId must be in the database");
        }
    }

    public void validateRequestTimeDifference(LocalDateTime createdAt, Long requesterId, Long receiverId) {
        LocalDateTime currentRequestTime = createdAt;
        LocalDateTime latestRequestTime = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId).get().getCreatedAt();

        if (ChronoUnit.MONTHS.between(currentRequestTime, latestRequestTime) < 6) {
            throw new IllegalArgumentException("Sorry, but you can create recommendation request only once every 6 months.\n" +
                    "Your latest recommendation request create time: " + latestRequestTime);
        }
    }

    public void validateRequestedSkills(List<SkillRequest> skills) {
        if (!skills.stream()
                .map(requestSkill -> requestSkill.getSkill().getTitle()).allMatch(skillRepository::existsByTitle)) {
            throw new IllegalArgumentException("Not all requested skill exists");
        }
    }
}
