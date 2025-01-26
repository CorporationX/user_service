package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {

    private final RecommendationRequestRepository requestRepository;

    private final RecommendationRequestMapper recommendationRequestMapper;

    private final UserRepository userRepository;

    private final SkillRepository skillRepository;

    private final SkillRequestRepository skillRequestRepository;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        log.info("Creating recommendation request: {}", dto);
        validateRequest(dto);

        RecommendationRequest request = recommendationRequestMapper.toEntity(dto);
        request = requestRepository.save(request);
        saveSkillRequests(request, dto.getSkills());
        log.info("Request recommendation created successfully with ID: {}", request.getId());

        return recommendationRequestMapper.toDto(request);
    }

    private void saveSkillRequests(RecommendationRequest request, List<Long> skillIds) {
        if (skillIds == null) {
            return;
        }
        for (Long skillId : skillIds) {
            Skill skill = skillRepository.findById(skillId).orElseThrow(() -> {
                log.error("Skill with ID {} not found", skillId);
                return new IllegalArgumentException("Skill with ID " + skillId + " not found");
            });
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(request);
            skillRequest.setSkill(skill);
            skillRequestRepository.save(skillRequest);
        }
    }

    private void validateRequest(RecommendationRequestDto dto) {
        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            log.warn("Validation failed: Message is empty or null. DTO: {}", dto);
            throw new IllegalArgumentException("Message cannot be empty");
        }

        if (!userRepository.existsById(dto.getRequesterId()) || !userRepository.existsById(dto.getReceiverId())) {
            log.error("Validation failed: One or two users do not exist. RequesterId: {}, ReceiverId: {}",
                    dto.getRequesterId(), dto.getReceiverId());
            throw new IllegalArgumentException("Users must exist");
        }

        if (requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId())
                .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6)))
                .isPresent()) {
            log.warn("Request already exists within the past 6 months DTO: {}", dto);
            throw new IllegalArgumentException("Request already exists within the past 6 months");
        }

        if (dto.getSkills() != null) {
            for (Long skillId : dto.getSkills()) {
                if (!skillRepository.existsById(skillId)) {
                    log.warn("Skill with ID: {} does not exist", skillId);
                    throw new IllegalArgumentException("Skill with ID " + skillId + " does not exist");
                }
            }
        }
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        log.info("Rejecting recommendation request ID: {}", id);
        RecommendationRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            log.error("Recommendation request with ID {} not found", id);
            throw new IllegalStateException("Cannot reject a non-pending request");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        request = requestRepository.save(request);
        log.info("Recommendation request ID: {} rejected successfully", id);
        return recommendationRequestMapper.toDto(request);
    }

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        if (recommendationRequest == null) {
            throw new IllegalArgumentException("Recommendation request cannot be null.");
        }

        validateRequest(recommendationRequest);

        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        return create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        log.info("Getting recommendation requests with filter: {}", filter);
        List<RecommendationRequest> requests = requestRepository.findAll();

        List<RecommendationRequestDto> result = requests.stream()
                .filter(request -> filterMatches(request, filter))
                .map(recommendationRequestMapper::toDto)
                .toList();
        log.info("Recommendation requests matching filter: {}", filter);
        return result;
    }

    public RecommendationRequestDto getRecommendationRequests(long id) {
        RecommendationRequest request = requestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Recommendation request with ID {} not found", id);
                    return new EntityNotFoundException("RecommendationRequest not found");
                });
        return recommendationRequestMapper.toDto(request);
    }

    private boolean filterMatches(RecommendationRequest request, RequestFilterDto filter) {
        return (filter.getStatus() == null || request.getStatus() == filter.getStatus()) &&
                (filter.getRequesterId() == null || request.getRequester().getId().equals(filter.getRequesterId())) &&
                (filter.getReceiverId() == null || request.getReceiver().getId().equals(filter.getReceiverId()));
    }
}