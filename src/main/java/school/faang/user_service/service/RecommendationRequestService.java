package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    @Transactional
    public void create(RecommendationRequestDto recommendationRequestDto) {
        validateRecommendationRequest(recommendationRequestDto);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);

        long requesterId = recommendationRequest.getRequester().getId();
        long receiverId = recommendationRequest.getReceiver().getId();
        String message = recommendationRequest.getMessage();

        if (isUserNotExists(requesterId)) {
            throw new IllegalArgumentException("Requester with such id: " + requesterId + " does not exist.");
        }

        if (isUserNotExists(receiverId)) {
            throw new IllegalArgumentException("Receiver with such id: " + receiverId + " does not exist.");
        }

        if (hasPendingRequest(requesterId, receiverId)) {
            throw new IllegalArgumentException("A recommendation request between the same users can only be sent once every six months.");
        }

        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());
        recommendationRequest.setStatus(RequestStatus.PENDING);

        RecommendationRequest savedRequest = recommendationRequestRepository.create(requesterId,receiverId, message);

        for (SkillRequest skillRequest : recommendationRequest.getSkills()) {
            skillRequest.setRequest(savedRequest);
            skillRequestRepository.create(requesterId,skillRequest.getId());
        }
    }

    private boolean isUserNotExists(Long userId) {
        return !userRepository.existsById(userId);
    }

    private void validateRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto.getMessage() == null || recommendationRequestDto.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Recommendation request message cannot be empty.");
        }
        List<Long> skillsIds = recommendationRequestDto.getSkillsIds();
        if (skillsIds == null || skillsIds.isEmpty()) {
            throw new IllegalArgumentException("Recommendation request must contain at least one skill.");
        }
    }

    private boolean hasPendingRequest(long requesterId, long receiverId) {
        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);
        if (latestPendingRequest.isPresent()) {
            LocalDateTime createdAt = latestPendingRequest.get().getCreatedAt();
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            return createdAt.isAfter(sixMonthsAgo);
        }
        return false;
    }
}
