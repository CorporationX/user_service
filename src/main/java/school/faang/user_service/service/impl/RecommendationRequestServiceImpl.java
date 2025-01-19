package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.request.RecommendationRequestDto;
import school.faang.user_service.dto.request.RejectionDto;
import school.faang.user_service.dto.request.SearchRequest;
import school.faang.user_service.dto.response.RecommendationRequestResponseDto;
import school.faang.user_service.dto.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationFrequencyException;
import school.faang.user_service.repository.genericSpecification.GenericSpecification;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static school.faang.user_service.dto.entity.RequestStatus.PENDING;
import static school.faang.user_service.dto.entity.RequestStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public String requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        Long requesterId = recommendationRequestDto.getRequesterId();
        validateUserExistence(requesterId);
        validateUserExistence(recommendationRequestDto.getReceiverId());
        validateRecommendationRequestFrequency(requesterId);
        validateSkillsExist(recommendationRequestDto.getSkillIds());
        RecommendationRequest recommendationRequest = createRecommendationRequest(recommendationRequestDto);
        associateRecommendationRequestWithSkill(recommendationRequest.getId(),
                recommendationRequestDto.getSkillIds());
        return "Successfully completed";
    }

    @Override
    public RecommendationRequestResponseDto getById(Long recommendationId) {
        RecommendationRequest foundEntity = findRecommendationRequestById(recommendationId);
        return recommendationRequestMapper.toResponse(foundEntity);
    }

    @Override
    public String rejectRequest(Long recommendationRequestId, RejectionDto rejectionDto) {
        Optional<RecommendationRequest> foundEntity = recommendationRequestRepository
                .findRecommendationRequestByIdAndStatus(recommendationRequestId, PENDING);
        if (foundEntity.isEmpty()) {
            throw new EntityNotFoundException("Recommendation request not found");
        }
        RecommendationRequest recommendationRequest = foundEntity.get();
        recommendationRequest.setRejectionReason(rejectionDto.getRejectionReason());
        recommendationRequest.setStatus(REJECTED);
        recommendationRequest.setUpdatedAt(LocalDateTime.now());
        recommendationRequestRepository.save(recommendationRequest);
        return "Recommendation successfully rejected";
    }

    @Override
    public List<RecommendationRequestResponseDto> search(SearchRequest request) {
        GenericSpecification<RecommendationRequest> spec = new GenericSpecification<>(
                RecommendationRequest.class, request.getRootGroup(), request.getSort());
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll(spec);
        return recommendationRequestMapper.toResponse(recommendationRequests);
    }

    private void validateUserExistence(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User not found with id: %d", userId));
        }
    }

    private void validateRecommendationRequestFrequency(Long requesterId) {
        Optional<RecommendationRequest> recommendationRequest = recommendationRequestRepository
                .findLatestRecommendationInLast6Months(requesterId);
        if (recommendationRequest.isPresent()) {
            throw new RecommendationFrequencyException(String.format(
                    "User with ID %d cannot send a new recommendation request. Last request was sent at %s. " +
                            "At least 6 months should elapse.",
                    requesterId,
                    recommendationRequest.get().getCreatedAt()));
        }
    }

    private void validateSkillsExist(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new IllegalArgumentException("You have not provided any skill IDs");
        }

        Long existingSkillCount = skillRepository.getExistingSkillCountByIds(skillIds);
        if (existingSkillCount != skillIds.size()) {
            throw new IllegalArgumentException("Some provided skill IDs do not exist in the database");
        }
    }

    private RecommendationRequest createRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequest);
        return recommendationRequestRepository.save(entity);
    }

    private void associateRecommendationRequestWithSkill(Long recommendationRequestId, List<Long> skillIds) {
        for (Long id : skillIds) {
            skillRequestRepository.create(recommendationRequestId, id);
        }
    }

    private RecommendationRequest findRecommendationRequestById(Long recommendationRequestId) {
        return recommendationRequestRepository.findById(recommendationRequestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Recommendation request not found with ID: %d", recommendationRequestId)
                ));
    }

}
