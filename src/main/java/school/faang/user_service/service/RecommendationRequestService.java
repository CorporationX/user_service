package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);

    Long requestId = recommendationRequest.getRequester().getId();
    Long receiverId = recommendationRequest.getReceiver().getId();
    String message = recommendationRequest.getMessage();

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);

        Long requestId = recommendationRequest.getRequester().getId();
        Long receiverId = recommendationRequest.getReceiver().getId();
        String message = recommendationRequest.getMessage();

        validateUsersExist(recommendationRequest);
        validateSkillsExist(recommendationRequest);
        validateRequestPeriod(recommendationRequest);

        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());
        recommendationRequest.setStatus(RequestStatus.PENDING);

        RecommendationRequest savedRequest = recommendationRequestRepository.
                create(requestId, receiverId, message);

        for (SkillRequest skillRequest : recommendationRequest.getSkills()) {
            skillRequest.setRequest(savedRequest);
            skillRequestRepository.create(requestId,skillRequest.getId());
        }
    }

    public void validateUsersExist(RecommendationRequestDto recommendationRequest) {
        if (!userRepository.existsById(recommendationRequest.getRequesterId()) || !userRepository.existsById(recommendationRequest.getReceiverId())) {
            throw new IllegalArgumentException("User not found");
        }
    }

    public void validateSkillsExist(RecommendationRequestDto recommendationRequest) {
        List<SkillRequest> skills = recommendationRequest.getSkills();
        if (skills == null || skills.isEmpty()) {
            throw new IllegalArgumentException("Recommendation request must contain at least one skill.");
        }
    }

    public void validateRequestPeriod (RecommendationRequestDto recommendationRequest) {
        boolean canRequestRecommendation = recommendationRequestRepository.checkRequestAllowed(recommendationRequest.getRequesterId(), recommendationRequest.getRecieverId(), LocalDateTime.now().minusMonths(6));
        if (!canRequestRecommendation) {
            throw new IllegalArgumentException("Request not allowed");
        }
    }
}





