package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int SIX_MONTHS_IN_DAYS = 180;

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;

    public void create(RecommendationRequest recommendationRequest) {
        validationRecommendationRequestForCreateMethod(recommendationRequest);
        saveRecommendationRequest(recommendationRequest);
        recommendationRequest.getSkills()
                .forEach(skillRequest -> skillRequestRepository
                        .create(recommendationRequest.getId(), skillRequest.getId()));
    }

    private void validationRecommendationRequestForCreateMethod(RecommendationRequest recommendationRequest) {
        if (getById(recommendationRequest.getRequester()).isEmpty() ||
                getById(recommendationRequest.getReceiver()).isEmpty()) {
            throw new IllegalArgumentException();
        }
        Optional<RecommendationRequest> latestPendingRequest = getLatestRecommendationRequest(recommendationRequest);
        if (!allSkillsPresent(recommendationRequest)) {
            throw new IllegalArgumentException();
        }
        if (latestPendingRequest.isPresent()) {
            if (DAYS.between(latestPendingRequest.get().getCreatedAt(),
                    LocalDateTime.now()) < SIX_MONTHS_IN_DAYS) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void saveRecommendationRequest(RecommendationRequest recommendationRequest) {
        recommendationRequestRepository.create(
                recommendationRequest.getRequester().getId(),
                recommendationRequest.getReceiver().getId(),
                recommendationRequest.getMessage(),
                recommendationRequest.getStatus(),
                recommendationRequest.getRecommendation().getId());
    }

    private Optional<User> getById(User user) {
        return userRepository.findById(user.getId());
    }

    private boolean allSkillsPresent(RecommendationRequest recommendationRequest) {
        List<Long> listIds = recommendationRequest.getSkills().stream()
                .map(SkillRequest::getId)
                .toList();
        List<Skill> allSkillsById = skillRepository.findAllById(listIds);
        if (!(allSkillsById.size() == recommendationRequest.getSkills().size())) {
            throw new IllegalArgumentException();
        }
        return true;
    }

    private Optional<RecommendationRequest> getLatestRecommendationRequest(
            RecommendationRequest recommendationRequest
    ) {
        return recommendationRequestRepository
                .findLatestPendingRequest(
                        recommendationRequest.getRequester().getId(),
                        recommendationRequest.getReceiver().getId());
    }

    public List<RecommendationRequest> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> recommendationRequests =
                (List<RecommendationRequest>) recommendationRequestRepository.findAll();
        return recommendationRequests.stream()
                .filter(request -> applyFilterToRequest(request, filter))
                .toList();
    }

    private boolean applyFilterToRequest(RecommendationRequest request, RequestFilterDto filter) {
        return request.getCreatedAt() == filter.getCreatedAt()
                && request.getRequester().getId() == filter.getRequesterId()
                && request.getReceiver().getId() == filter.getReceiverId()
                && request.getStatus() == filter.getStatus()
                && request.getRejectionReason().equals(filter.getRejectionReason())
                && request.getRecommendation().getId() == filter.getRecommendationId();
    }

    public RecommendationRequest getRequest(long id) {
        Optional<RecommendationRequest> request = recommendationRequestRepository.findById(id);
        if (request.isPresent()) {
            return request.get();
        } else {
            throw new NullPointerException();
        }
    }

    public RecommendationRequest rejectRequest(long id, RejectionDto rejectionDto) {
        RecommendationRequest request = getRequest(id);
        if (request.getStatus() == RequestStatus.REJECTED || request.getStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException();
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getReason());
        return request;
    }
}
