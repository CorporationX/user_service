package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRejectDto;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationResponseDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.MessagePatternFilter;
import school.faang.user_service.filter.recommendation.ReceiverIdFilter;
import school.faang.user_service.filter.recommendation.RecommendationFilter;
import school.faang.user_service.filter.recommendation.RequesterIdFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RecommendationFilter> filters;

    @Transactional
    public RecommendationResponseDto create(RecommendationRequestDto recommendationRequest) {
        try {
            Long requesterId = recommendationRequest.requesterId();
            Long receiverId = recommendationRequest.receiverId();

            if (requesterId.equals(receiverId)) {
                throw new DataValidationException("Requester and receiver cannot be the same");
            }

            User requester = userRepository.findById(requesterId)
                    .orElseThrow(() -> new EntityNotFoundException("Requester not found with id: " + requesterId));
            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new EntityNotFoundException("Receiver not found with id: " + receiverId));

            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            Optional<RecommendationRequest> latestRequest = recommendationRequestRepository.findLatestPendingRequest(
                    requesterId, receiverId);

            if (latestRequest.isPresent() && latestRequest.get().getCreatedAt().isAfter(sixMonthsAgo)) {
                throw new DataValidationException("You can send a recommendation request to this user only once per 6 months");
            }

            if (recommendationRequest.skills() == null || recommendationRequest.skills().isEmpty()) {
                throw new DataValidationException("Skills list must not be empty!");
            }

            RecommendationRequest newRequest = recommendationRequestMapper.toEntity(recommendationRequest);
            newRequest.setRequester(requester);
            newRequest.setReceiver(receiver);
            newRequest.setStatus(RequestStatus.PENDING);

            RecommendationRequest savedRequest = recommendationRequestRepository.save(newRequest);

            recommendationRequest.skills().forEach(skillTitle -> {
                Skill skill = skillRepository.findByTitle(skillTitle)
                        .orElseThrow(() -> new EntityNotFoundException("Skill not found: " + skillTitle));

                SkillRequest skillRequest = new SkillRequest();
                skillRequest.setRequest(savedRequest);
                skillRequest.setSkill(skill);
                skillRequestRepository.save(skillRequest);
            });

            return recommendationRequestMapper.toDto(savedRequest);
        } catch (Exception e) {
            log.error("Failed to create recommendation request", e);
            throw new DataValidationException("Failed to create recommendation request: " + e.getMessage());
        }
    }
    public List<RecommendationResponseDto> getRequests(@Valid RequestFilterDto filter) {
        List<RecommendationRequest> allRequests = recommendationRequestRepository.findAll();
        List<RecommendationFilter> filters = List.of(
                new RequesterIdFilter(),
                new ReceiverIdFilter(),
                new MessagePatternFilter()
        );

        Stream<RecommendationRequest> requestStream = allRequests.stream();
        for (RecommendationFilter recommendationFilter : filters) {
            if (recommendationFilter.isApplicable(filter)) {
                requestStream = recommendationFilter.apply(requestStream, filter);
            }
        }

        return requestStream
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationResponseDto getRequest(long id) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation request not found with id: " + id));
        return recommendationRequestMapper.toDto(request);
    }

    @Transactional
    public RecommendationResponseDto rejectRequest(long id, @Valid RecommendationRejectDto rejection) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation request not found with id: " + id));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Cannot reject request: current status is " + request.getStatus());
        }

        request.setRejectionReason(rejection.reason());
        request.setStatus(RequestStatus.REJECTED);
        RecommendationRequest updatedRequest = recommendationRequestRepository.save(request);

        return recommendationRequestMapper.toDto(updatedRequest);
    }
    private boolean filterByRequesterId(RecommendationRequest request, Long requesterId) {
        return requesterId == null || request.getRequester().getId().equals(requesterId);
    }

    private boolean filterByReceiverId(RecommendationRequest request, Long receiverId) {
        return receiverId == null || request.getReceiver().getId().equals(receiverId);
    }

    private boolean filterByRecommendationId(RecommendationRequest request, Long recommendationId) {
        return recommendationId == null ||
                (request.getRecommendation() != null &&
                        request.getRecommendation().getId() == recommendationId);
    }

    private boolean filterByMessagePattern(RecommendationRequest request, String messagePattern) {
        return messagePattern == null ||
                (request.getMessage() != null &&
                        request.getMessage().toLowerCase().contains(messagePattern.toLowerCase()));
    }

    private boolean filterByCreatedAfter(RecommendationRequest request, LocalDateTime createdAfter) {
        return createdAfter == null || request.getCreatedAt().isAfter(createdAfter);
    }

    private boolean filterByCreatedBefore(RecommendationRequest request, LocalDateTime createdBefore) {
        return createdBefore == null || request.getCreatedAt().isBefore(createdBefore);
    }
}