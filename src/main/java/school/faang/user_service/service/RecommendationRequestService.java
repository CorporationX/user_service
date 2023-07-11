package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final static String REQUESTER_OR_RECEIVER_NOT_FOUND = "Requester or receiver not found";
    private final static String REQUEST_IS_PENDING = "Request is pending";
    private final static String SKILL_NOT_FOUND = "Skill not found";

    private final RecommendationRequestRepository recommendationRepository;
    private final SkillRequestRepository skillRepository;
    private final UserRepository userRepository;

    public void create(RecommendationRequestDto recommendationRequest) {
        if (isRequestAvailable(recommendationRequest)) {
            long requesterId = recommendationRequest.getRequesterId();
            long receiverId = recommendationRequest.getReceiverId();
            String message = recommendationRequest.getMessage();
            recommendationRepository.create(requesterId, receiverId, message);
            recommendationRequest.getSkills()
                    .forEach(skill -> skillRepository.create(requesterId, skill.getId()));
        }
    }

    private boolean isUserExist(long requesterId, long receiverId) {
        Optional<User> requester = userRepository.findById(requesterId);
        Optional<User> receiver = userRepository.findById(receiverId);
        return requester.isPresent() && receiver.isPresent();
    }

    private boolean isRequestAvailable(RecommendationRequestDto recommendationRequest) {
        long receiverId = recommendationRequest.getReceiverId();
        long requesterId = recommendationRequest.getRequesterId();
        Optional<RecommendationRequest> lastRequest;

        if (!isUserExist(requesterId, receiverId)) {
            throw new IllegalArgumentException(REQUESTER_OR_RECEIVER_NOT_FOUND);
        }

        lastRequest = recommendationRepository.findLatestPendingRequest(requesterId, receiverId);
        if (lastRequest.isPresent()) {
            LocalDateTime last = lastRequest.get().getUpdatedAt();
            LocalDateTime cur = recommendationRequest.getCreatedAt();
            if (last.plus(6, ChronoUnit.MONTHS).isBefore(cur)) {
                throw new DateTimeException(REQUEST_IS_PENDING);
            }
        }

        checkSkills(recommendationRequest);

        return true;
    }

    private void checkSkills(RecommendationRequestDto recommendationRequest) {
        recommendationRequest.getSkills().stream()
                .mapToLong(Skill::getId)
                .boxed()
                .forEach(skillId -> {
                    if (!skillRepository.existsById(skillId)) {
                        throw new IllegalArgumentException(SKILL_NOT_FOUND);
                    }
                });
    }
}
