package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filters.recommendation.request.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper requestMapper;
    private final List<RecommendationRequestFilter> recRequestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto recRequestDto) {
        RecommendationRequest request = requestMapper.toEntity(recRequestDto);

        if (eligibleForRecommendation(recRequestDto)) {
            long createdRequestId = recommendationRepository.create(
                    request.getRequester().getId(),
                    request.getReceiver().getId(),
                    request.getMessage()
            );

            request.setId(createdRequestId);
            recRequestDto.getSkillsId().forEach(skillId -> skillRequestRepository.create(recRequestDto.getId(), skillId));
        }
        return requestMapper.toDto(request);
    }

    private boolean usersExistInDb(RecommendationRequestDto recRequestDto) {
        boolean usersExist = recommendationRequestRepository.checkTheUsersExistInDb(
                recRequestDto.getRequesterId(), recRequestDto.getReceiverId());
        if (!usersExist) {
            log.error("The users don't exist in database");
        }
        return usersExist;
    }

    private boolean sixMonthHavePassed(RecommendationRequestDto recommendationRequest) {
        Optional<RecommendationRequest> latestRequest = recommendationRequestRepository.findLatestPendingRequest(
                recommendationRequest.getRequesterId(), recommendationRequest.getReceiverId());

        if (latestRequest.isEmpty()) {
            return true;
        }

        LocalDateTime lastRequestDate = latestRequest.get().getCreatedAt();
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        return lastRequestDate.isBefore(sixMonthsAgo);
    }

    private boolean skillsExistInDb(RecommendationRequestDto recommendationRequest) {
        return skillRepository.countExisting(recommendationRequest
                .getSkillsId()) == recommendationRequest.getSkillsId().size();
    }

    private boolean eligibleForRecommendation(RecommendationRequestDto recRequest) {
        return usersExistInDb(recRequest)
                && sixMonthHavePassed(recRequest)
                && skillsExistInDb(recRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();

        for (RecommendationRequestFilter filter : recRequestFilters) {
            if (filter.isApplicable(filters)) {
                requests = filter.apply(requests, filters);
            }
        }

        return requestMapper.toDtoList(requests.toList());
    }

    public RecommendationRequestDto getRequest(Long requestId) {
        RecommendationRequest resultRequest = recommendationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("No request found by id: " + requestId));
        return requestMapper.toDto(resultRequest);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        if (rejectionDto.getReason() == null || rejectionDto.getReason().isBlank()) {
            throw new IllegalArgumentException("Rejection reason not found");
        }

        RecommendationRequestDto recRequest = getRequest(id);

        if (recRequest.getRejectionReason() == null || recRequest.getRejectionReason().isBlank()) {
            recommendationRequestRepository.rejectRequest(id, rejectionDto.getReason());
            recRequest.setRejectionReason(rejectionDto.getReason());
        } else {
            log.error("Request already rejected");
        }
        return recRequest;
    }
}
