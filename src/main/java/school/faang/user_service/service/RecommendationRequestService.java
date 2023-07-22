package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRepository;
    private final SkillRequestRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public void create(RecommendationRequestDto dtoRequest) {
        if (dtoRequest.getMessage() == null || dtoRequest.getMessage().isEmpty()){
            throw new IllegalArgumentException("Empty recommendation request!");
        }
        List<Skill> skills = dtoRequest.getSkills();
        if (skills == null || skills.isEmpty()){
            throw new IllegalArgumentException("Recommendation request can't be without any skills!");
        }
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dtoRequest);

        long requesterID = recommendationRequest.getRequester().getId();
        long receiverID = recommendationRequest.getReceiver().getId();
        String message = recommendationRequest.getMessage();

        if (userRepository.existsById(requesterID) && userRepository.existsById(receiverID)) {
            if (!hasPending(requesterID, receiverID)) {
                recommendationRequest.setCreatedAt(LocalDateTime.now());
                recommendationRequest.setUpdatedAt(LocalDateTime.now());
                recommendationRequest.setStatus(RequestStatus.PENDING);
                recommendationRepository.create(requesterID, receiverID, message);

                recommendationRequest.getSkills().forEach(skill -> skillRepository.create(requesterID, skill.getId()));
            } else {
                throw new IllegalArgumentException("A recommendation request between the same users can only be sent once every six months!");
            }
        } else {
            throw new IllegalArgumentException("Requester or receiver does not exist!");
        }
    }

    private boolean hasPending(long requesterId, long receiverId) {
        Optional<RecommendationRequest> latestRequest = recommendationRepository.
                findLatestPendingRequest(requesterId, receiverId);
        if (latestRequest.isPresent()) {
            LocalDateTime createdAt = latestRequest.get().getCreatedAt();
            LocalDateTime sixMonthAgo = LocalDateTime.now().minusMonths(6);
            return createdAt.isAfter(sixMonthAgo);
        }
        return false;
    }
}
