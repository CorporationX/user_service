package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationRequestException;
import school.faang.user_service.filter.ReceiverIdFilter;
import school.faang.user_service.filter.RequesterIdFilter;
import school.faang.user_service.filter.StatusFilter;
import school.faang.user_service.repository.RequestFilter;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService{
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Recommendation request not found for id: " + id));

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new RecommendationRequestException("Recommendation request has already been processed (accepted or rejected).");
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getReason());

        RecommendationRequest updatedRequest = recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(updatedRequest);
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new RecommendationRequestException("Recommendation Request not found for id: " + id));

        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RequestFilter> filters = createFilters(filter);

        List<RecommendationRequest> allRequests = recommendationRequestRepository.findAll();

        List<RecommendationRequest> filteredRequests = allRequests.stream()
                .filter(request -> filters.stream().allMatch(f -> f.apply(request)))
                .collect(Collectors.toList());

        return recommendationRequestMapper.toDtoList(filteredRequests);
    }

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        if (!userRepository.existsById(recommendationRequestDto.getRequesterId())) {
            throw new NullPointerException("Requester not found");
        }

        if (!userRepository.existsById(recommendationRequestDto.getReceiverId())) {
            throw new NullPointerException("Receiver not found");
        }

        recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId())
                .ifPresent(request -> {
                    if (ChronoUnit.MONTHS.between(request.getCreatedAt(), LocalDateTime.now()) < 6) {
                        throw new RecommendationRequestException("Recommendation request can be sent only once in 6 months");
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
                throw new RecommendationRequestException("One or more skills do not exist for the requester");
            }
        }
    }

    private void saveSkills(RecommendationRequestDto recommendationRequestDto, Long requestId) {
        recommendationRequestDto.getSkillId().forEach(skillId ->
                skillRequestRepository.create(requestId, skillId)
        );
    }

    private List<RequestFilter> createFilters(RequestFilterDto filter) {
        List<RequestFilter> filters = new ArrayList<>();
        if (filter.getRequesterId() != null) {
            filters.add(new RequesterIdFilter(filter.getRequesterId()));
        }
        if (filter.getReceiverId() != null) {
            filters.add(new ReceiverIdFilter(filter.getReceiverId()));
        }
        if (filter.getStatus() != null) {
            filters.add(new StatusFilter(filter.getStatus()));
        }
        return filters;
    }
}
