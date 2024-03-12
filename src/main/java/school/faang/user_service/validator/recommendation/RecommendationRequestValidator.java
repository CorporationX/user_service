package school.faang.user_service.validator.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.handler.exception.EntityExistException;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;

    public void validateSkills(List<Long> skillIds) {
        if (skillIds != null && !skillIds.isEmpty()) {
            skillIds.forEach(skillId -> {
                if (!skillRequestRepository.existsById(skillId)) {
                    throw new EntityNotFoundException("Skill id not found" + skillId);
                }
            });
        }
    }

    public void validateUser(long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new EntityNotFoundException("User by id " + userId + " not found");
        }
    }

    public void validateRequestFrequency(long requesterId, long receiverId) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);

        recommendationRequest.ifPresent(request -> {
            if (request.getCreatedAt().plusMonths(6).isAfter(LocalDateTime.now())) {
                throw new EntityExistException("Запрос рекомендации от одного и того же пользователя к другому можно отправлять не чаще, чем один раз в 6 месяцев");
            }
        });
    }

    public void validate(RecommendationRequestDto recommendationRequestDto) {
        long requesterId = recommendationRequestDto.getRequesterId();
        long receiverId = recommendationRequestDto.getReceiverId();

        validateUser(requesterId);
        validateUser(receiverId);
        validateRequestFrequency(requesterId, receiverId);
        validateSkills(recommendationRequestDto.getSkillIds());
    }
}
