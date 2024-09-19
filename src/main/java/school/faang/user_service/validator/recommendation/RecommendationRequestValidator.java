package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final static long MONTH_FOR_SEARCH_REQUEST = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;

    public void validateRequesterAndReceiver(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto.getRequesterId().equals(recommendationRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("Requester and receiver cannot be the same person.");
        }
        if (!userRepository.existsById(recommendationRequestDto.getRequesterId())) {
            throw new IllegalArgumentException("Requester with ID " + recommendationRequestDto.getRequesterId() + " does not exist.");
        }
        if (!userRepository.existsById(recommendationRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("Receiver with ID " + recommendationRequestDto.getReceiverId() + " does not exist.");
        }
    }

    public void validateRequestAndCheckTimeLimit(RecommendationRequestDto recommendationRequestDto) {
        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository.findLatestPendingRequest(
                recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId());
        if (latestPendingRequest.isPresent()) {
            if (latestPendingRequest.get().getUpdatedAt().plusMonths(MONTH_FOR_SEARCH_REQUEST).isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Not enough time has passed since the last request.");
            }
        }
    }

    public void validateSkillRequest(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestDto.getSkillsId()
                .forEach(skillId -> {
                    if (!skillRequestRepository.existsById(skillId)) {
                        throw new IllegalArgumentException("Skill with ID " + skillId + " does not exist.");
                    }
                });
    }

}
