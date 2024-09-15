package school.faang.user_service.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;

    @Autowired
    public RecommendationRequestService(RecommendationRequestRepository recommendationRequestRepository,
                                        SkillRepository skillRepository,
                                        SkillRequestRepository skillRequestRepository,
                                        RecommendationRequestMapper recommendationRequestMapper, UserRepository userRepository) {
        this.recommendationRequestRepository = recommendationRequestRepository;
        this.skillRequestRepository = skillRequestRepository;
        this.recommendationRequestMapper = recommendationRequestMapper;
        this.userRepository = userRepository;
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recommendation request not found for id: " + id));

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Recommendation request has already been processed (accepted or rejected).");
        }

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getReason());

        RecommendationRequest updatedRequest = recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(updatedRequest);
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recommendation Request not found for id: " + id));

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

        if (!userRepository.existsById(recommendationRequestDto.getRequesterId())) {
            throw new IllegalArgumentException("Requester not found");
        }

        // Проверяем наличие Receiver
        if (!userRepository.existsById(recommendationRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("Receiver not found");
        }

        recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId())
                .ifPresent(request -> {
                    if (ChronoUnit.MONTHS.between(request.getCreatedAt(), LocalDateTime.now()) < 6) {
                        throw new IllegalArgumentException("Recommendation request can be sent only once in 6 months");
                    }
                });

        // Проверяем, владеет ли пользователь всеми указанными навыками
        if (recommendationRequestDto.getSkillId() != null && !recommendationRequestDto.getSkillId().isEmpty()) {
            int ownedSkillsCount = userRepository.countOwnedSkills(
                    recommendationRequestDto.getRequesterId(),
                    recommendationRequestDto.getSkillId()
            );

            if (ownedSkillsCount != recommendationRequestDto.getSkillId().size()) {
                throw new IllegalArgumentException("One or more skills do not exist for the requester");
            }
        }

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        RecommendationRequest savedRequest = recommendationRequestRepository.save(recommendationRequest);

        if (recommendationRequestDto.getSkillId() != null) {
            recommendationRequestDto.getSkillId().forEach(skillId ->
                    skillRequestRepository.create(savedRequest.getId(), skillId)
            );
        }

        return recommendationRequestMapper.toDto(savedRequest);
    }

}