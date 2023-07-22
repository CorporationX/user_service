package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
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
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create (RecommendationRequestDto recommendationRequest) {

        validateUsersExist(recommendationRequest);
        validateSkillsExist(recommendationRequest);
        validateRequestPeriod(recommendationRequest);

        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.toEntity(recommendationRequest);

        long requesterID = recommendationRequestEntity.getRequester().getId();
        long receiverID = recommendationRequestEntity.getReceiver().getId();
        String message = recommendationRequestEntity.getMessage();

        recommendationRequestRepository.create(requesterID, receiverID, message);
        recommendationRequestEntity.getSkills().forEach(skill -> skillRequestRepository.create(requesterID, skill.getId()));
        return null;
    }

    public void validateUsersExist(RecommendationRequestDto recommendationRequest) {
        if (!userRepository.existsById(recommendationRequest.getRequesterId()) || !userRepository.existsById(recommendationRequest.getReceiverId())) {
            throw new EntityNotFoundException("User not found");
        }
    }

    public void validateSkillsExist(RecommendationRequestDto recommendationRequest) {
        List<Long> skillIds = recommendationRequest.getSkills().stream().map(SkillRequest::getId).toList();
        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                throw new EntityNotFoundException("Such skill does not exist");
            }
        }
    }

    public void validateRequestPeriod (RecommendationRequestDto recommendationRequest) {
        long requesterId = recommendationRequest.getRequesterId();
        long receiverId = recommendationRequest.getReceiverId();
        Optional<RecommendationRequest> lastRequest = recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);
        LocalDateTime lastRequestsDate = lastRequest.get().getUpdatedAt();
        LocalDateTime currentRequestDate = recommendationRequest.getCreatedAt();

        if (lastRequest.isPresent() && currentRequestDate.minusMonths(6).isAfter(lastRequestsDate)) {
            throw new DataValidationException("A recommendation request from the same requester to the receiver has already been made in the last 6 months");
        }
    }
}