package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
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
    private static final String MESSAGE_REQUESTER_OR_RECEIVER_EMPTY = "Requester or Receiver not in the database";
    private static final String MESSAGE_NOT_ALL_SKILLS_IN_DB = "Not all skills are present in the database";
    private static final String MESSAGE_LAST_REQUEST_SENT_LESS_6_MONTHS_AGO = "The last request was sent less than 6 months ago";
    private static final String MESSAGE_RECOMMENDATION_REQUEST_NOT_IN_DB = "The recommendationRequest is not in the database";
    private static final String MESSAGE_RECOMMENDATION_REQUEST_REJECTED_OR_ACCEPTED = "RecommendationRequest rejected or accepted";

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper mapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        RecommendationRequest recommendationRequest = mapper.toEntity(recommendationRequestDto);
        validationRecommendationRequestForCreateMethod(recommendationRequest);
        saveRecommendationRequest(recommendationRequest);
        recommendationRequest.getSkills()
                .forEach(skillRequest -> skillRequestRepository
                        .create(recommendationRequest.getId(), skillRequest.getId()));
        return mapper.toDto(recommendationRequest);
    }

    private void validationRecommendationRequestForCreateMethod(RecommendationRequest recommendationRequest) {
        if (getById(recommendationRequest.getRequester()).isEmpty() ||
                getById(recommendationRequest.getReceiver()).isEmpty()) {
            throw new RuntimeException(MESSAGE_REQUESTER_OR_RECEIVER_EMPTY);
        }
        Optional<RecommendationRequest> latestPendingRequest = getLatestRecommendationRequest(recommendationRequest);
        allSkillsPresent(recommendationRequest);
        if (latestPendingRequest.isPresent()) {
            if (DAYS.between(latestPendingRequest.get().getCreatedAt(),
                    LocalDateTime.now()) < SIX_MONTHS_IN_DAYS) {
                throw new RuntimeException(MESSAGE_LAST_REQUEST_SENT_LESS_6_MONTHS_AGO);
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

    private void allSkillsPresent(RecommendationRequest recommendationRequest) {
        List<Long> listIds = recommendationRequest.getSkills().stream()
                .map(SkillRequest::getId)
                .toList();
        List<Skill> allSkillsById = skillRepository.findAllById(listIds);
        if (!(allSkillsById.size() == recommendationRequest.getSkills().size())) {
            throw new RuntimeException(MESSAGE_NOT_ALL_SKILLS_IN_DB);
        }
    }

    private Optional<RecommendationRequest> getLatestRecommendationRequest(
            RecommendationRequest recommendationRequest
    ) {
        return recommendationRequestRepository
                .findLatestPendingRequest(
                        recommendationRequest.getRequester().getId(),
                        recommendationRequest.getReceiver().getId());
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> recommendationRequests =
                (List<RecommendationRequest>) recommendationRequestRepository.findAll();
        return recommendationRequests.stream()
                .filter(request -> applyFilterToRequest(request, filter))
                .map(mapper::toDto)
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

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException(MESSAGE_RECOMMENDATION_REQUEST_NOT_IN_DB));
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        RecommendationRequestDto requestDto = getRequest(id);
        RecommendationRequest request = mapper.toEntity(requestDto);
        if (request.getStatus() == RequestStatus.REJECTED || request.getStatus() == RequestStatus.ACCEPTED) {
            throw new RuntimeException(MESSAGE_RECOMMENDATION_REQUEST_REJECTED_OR_ACCEPTED);
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getReason());
        return mapper.toDto(request);
    }
}
