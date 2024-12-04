package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationRequestValidationException;
import school.faang.user_service.exception.RejectRequestFailedException;
import school.faang.user_service.service.filter.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recRequestFilters;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;

    @Value("${application.constants.max-month-limit-recommendation-request}")
    private int maxMonthLimitRecommendationRequest;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        User requester = getUserById(recommendationRequest.getRequesterId(), "requester");
        User receiver = getUserById(recommendationRequest.getReceiverId(), "receiver");
        List<Skill> skills = skillRepository.findAllById(recommendationRequest.getSkillIds());

        if (!canSendRecommendationRequest(recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId())
                || !skillExists(skills, recommendationRequest)
                || isMessageEmpty(recommendationRequest)) {
            log.error("Your request has failed due to the following reasons: You can only send requests once in 6 months or skills you provided are not found in the Database!");
            throw new RecommendationRequestValidationException("Your request did not meet the validation requirements!");
        }

        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.toEntity(recommendationRequest);
        recommendationRequestEntity.setStatus(RequestStatus.PENDING);
        recommendationRequestEntity.setRequester(requester);
        recommendationRequestEntity.setReceiver(receiver);
        recommendationRequestRepository.save(recommendationRequestEntity);
        return recommendationRequestMapper.toDto(recommendationRequestEntity);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        List<RecommendationRequest> requests = recommendationRequestRepository.findAll();
        recRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(requests.stream(), filters));
        log.info("A request to get the list of Recommendation Requests has been successfully processed!");
        return requests.stream()
                .map(recommendationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .map(recommendationRequestMapper::toDto)
                .orElseThrow(() -> {
                    log.info("A request has failed to get the recommendation request with an ID: {}", id);
                    throw new EntityNotFoundException("Request not found for ID: " + id);
                });
    }

    @Transactional
    public RejectionDto rejectRequest(long id, RejectionDto rejectionRequest) {
        recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("A request has failed to get the recommendation request with an ID: {}", id);
                    return new EntityNotFoundException("Request not found for ID: " + rejectionRequest.getId());
                });
        if (!rejectionRequest.getStatus().equals(RequestStatus.PENDING)) {
            log.error("A request without PENDING status attempted to cancel");
            throw new RejectRequestFailedException("You cannot reject the request without PENDING status");
        }
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(rejectionRequest);
        recommendationRequest.setRejectionReason(rejectionRequest.getReason());
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        log.info("A request to reject the recommendation request with an ID: {} has been successfully processed!", id);
        return recommendationRequestMapper.toRejectionDto(recommendationRequest);
    }

    private boolean canSendRecommendationRequest(Long requesterId, Long receiverId) {
        List<RecommendationRequest> requestsByRequesterAndReceiverIds = recommendationRequestRepository
                .findAll().stream()
                .filter(request -> request.getRequester() != null && request.getRequester().getId().equals(requesterId) && request.getReceiver().getId().equals(receiverId))
                .collect(Collectors.toList());
        if (!requestsByRequesterAndReceiverIds.isEmpty()) {
            LocalDateTime dateSixMonthLater = requestsByRequesterAndReceiverIds.get(0).getCreatedAt().plusMonths(maxMonthLimitRecommendationRequest);
            if (LocalDateTime.now().isBefore(dateSixMonthLater)) {
                return false;
            }
        }
        return true;
    }

    private boolean skillExists(List<Skill> skills, RecommendationRequestDto recommendationRequest) {
        return skills.size() == recommendationRequest.getSkillIds().size();
    }

    public boolean isMessageEmpty(RecommendationRequestDto recommendationRequest) {
        return recommendationRequest.getMessage().isEmpty();
    }

    private User getUserById(Long userId, String userType) {
        return userRepository.
                findById(userId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("A request has failed to get the %s user from DB with ID: %d", userType.toUpperCase(), userId);
                    log.error(errorMessage);
                    throw new EntityNotFoundException("User " + userType +  " not found in DB");
                });
    }
}
