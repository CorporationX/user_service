package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.util.recommendation.exception.RequestRecommendationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;

    public void validateSkills(List<Long> skillIds) {
        if (skillIds != null) {
            skillIds.forEach(skillId -> {
                if (!skillRequestRepository.existsById(skillId)) {
                    throw new RequestRecommendationException("Skill id not found" + skillId);
                }
            });
        }
    }

    public void validateRequester(long requesterId) {
        Optional<User> requester = userRepository.findById(requesterId);

        if (requester.isEmpty()) {
            throw new RequestRecommendationException("Requester not found");
        }
    }

    public void validateReceiver(long receiverId) {
        Optional<User> receiver = userRepository.findById(receiverId);

        if (receiver.isEmpty()) {
            throw new RequestRecommendationException("Receiver not found");
        }
    }

    public void validateRequestFrequency(long requesterId, long receiverId) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);

        recommendationRequest.ifPresent(request -> {
            if (request.getCreatedAt().plusMonths(6).isAfter(LocalDateTime.now())) {
                throw new RequestRecommendationException("Запрос рекомендации от одного и того же пользователя к другому можно отправлять не чаще, чем один раз в 6 месяцев");
            }
        });
    }

    public void validate(RecommendationRequestDto recommendationRequestDto) {
        long requesterId = recommendationRequestDto.getRequesterId();
        long receiverId = recommendationRequestDto.getReceiverId();
        validateRequester(requesterId);
        validateReceiver(receiverId);
        validateRequestFrequency(requesterId, receiverId);
        validateSkills(recommendationRequestDto.getSkillIds());
    }
}
