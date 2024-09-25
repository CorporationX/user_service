package school.faang.user_service.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.*;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation request not found for id: " + id));

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new RecommendationRequestAlreadyProcessedException("Recommendation request has already been processed (accepted or rejected).");
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getReason());

        RecommendationRequest updatedRequest = recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(updatedRequest);
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new RecommendationRequestNotFoundException("Recommendation Request not found for id: " + id));

        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> allRequests = recommendationRequestRepository.findAll();

        List<RecommendationRequest> filteredRequests = allRequests.stream()
                .filter(request -> {
                    if (filter.getRequesterId() != null && !request.getRequester().getId().equals(filter.getRequesterId())) {
                        return false;
                    }
                    if (filter.getReceiverId() != null && !request.getReceiver().getId().equals(filter.getReceiverId())) {
                        return false;
                    }
                    if (filter.getStatus() != null && !request.getStatus().equals(filter.getStatus())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        return recommendationRequestMapper.toDtoList(filteredRequests);
    }

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId())
                .ifPresent(request -> {
                    if (ChronoUnit.MONTHS.between(request.getCreatedAt(), LocalDateTime.now()) < 6) {
                        throw new RecommendationRequestTooFrequentException("Recommendation request can be sent only once in 6 months");
                    }
                });

        checkRequesterSkills(recommendationRequestDto.getRequesterId(), recommendationRequestDto.getSkillId());

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        RecommendationRequest savedRequest = recommendationRequestRepository.save(recommendationRequest);

        saveSkills(recommendationRequestDto, savedRequest.getId());

        return recommendationRequestMapper.toDto(savedRequest);
    }

    private void checkRequesterSkills(Long requesterId, List<Long> skillIds) {
        if (skillIds != null && !skillIds.isEmpty()) {
            int ownedSkillsCount = userRepository.countOwnedSkills(requesterId, skillIds);
            if (ownedSkillsCount != skillIds.size()) {
                throw new SkillOwnershipException("One or more skills do not exist for the requester");
            }
        }
    }

    private void saveSkills(RecommendationRequestDto recommendationRequestDto, Long requestId) {
        recommendationRequestDto.getSkillId().forEach(skillId ->
                skillRequestRepository.create(requestId, skillId)
        );
    }

    private boolean hasSkills(RecommendationRequestDto recommendationRequestDto) {
        return recommendationRequestDto.getSkillId() != null;
    }
}