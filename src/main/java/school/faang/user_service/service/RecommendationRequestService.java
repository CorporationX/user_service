package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;

    public void create(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);

        Long requesterId = recommendationRequest.getRequester().getId();
        Long receiverId = recommendationRequest.getReceiver().getId();

        if (!userRepository.existsById(requesterId)) {
            throw new IllegalArgumentException("");
        }
        if (!userRepository.existsById(receiverId)) {
            throw new IllegalArgumentException("");
        }
        if (sendRecommendationRequestIfEligible(requesterId, receiverId)) {
            throw new IllegalArgumentException("");
        }

        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        //

        for (SkillRequest skillRequest : recommendationRequest.getSkills()) {
            //save skills
        }
    }

    private boolean sendRecommendationRequestIfEligible(long requesterId, long receiverId) {
        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);
        if (latestPendingRequest.isPresent()) {
            LocalDateTime createdAt = latestPendingRequest.get().getCreatedAt();
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            return createdAt.isAfter(sixMonthsAgo);
        }
        return false;
    }
}
}
