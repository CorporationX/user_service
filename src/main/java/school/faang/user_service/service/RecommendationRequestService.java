package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.RecommendationFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MONTHS;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private static final int RECOMMENDATION_REQUEST_LIMIT = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationFilter recommendationFilter;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) throws EntityNotFoundException {
        var requester = findUserById(recommendationRequestDto.getRequesterId());
        var receiver = findUserById(recommendationRequestDto.getReceiverId());

        checkRecommendationFrequency(recommendationRequestDto.getCreatedAt(), recommendationRequestDto.getUpdatedAt());

        validateSkills(recommendationRequestDto.getSkillIds());

        var savedRecommendationRequest = saveRecommendationRequest(recommendationRequestDto);
        linkSkillsToRequest(savedRecommendationRequest, recommendationRequestDto.getSkillIds());

        return recommendationRequestMapper.toDto(savedRecommendationRequest);
    }

    private User findUserById(Long userId) throws EntityNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found."));
    }

    private void checkRecommendationFrequency(LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (isWithinSixMonthsTillNow(createdAt, updatedAt)) {
            throw new IllegalStateException("Recommendation requests can only be sent once every 6 months.");
        }
    }

    private void validateSkills(List<Long> skillRequestIds) throws EntityNotFoundException {
        boolean allSkillsExist = skillRequestIds.stream()
                .allMatch(id -> skillRequestRepository.findById(id).isPresent());
        if (!allSkillsExist) {
            throw new EntityNotFoundException("Some skills do not exist in the database.");
        }
    }

    private RecommendationRequest saveRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        var recommendationRequest = recommendationRequestMapper.toEntity(recommendationRequestDto);
        return recommendationRequestRepository.save(recommendationRequest);
    }

    private void linkSkillsToRequest(RecommendationRequest recommendationRequest, List<Long> skillRequestIds) {
        skillRequestIds.forEach(skill ->
                skillRequestRepository.create(recommendationRequest.getId(), skill));
    }

    private boolean isWithinSixMonthsTillNow(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return MONTHS.between(updatedAt, now()) < RECOMMENDATION_REQUEST_LIMIT ||
               MONTHS.between(createdAt, now()) < RECOMMENDATION_REQUEST_LIMIT;
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        return StreamSupport.stream(recommendationRequestRepository.findAll().spliterator(), false)
                .filter(recommendationRequest -> recommendationFilter.matchesFilter(recommendationRequest, filter))
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper.toDto(getRecommendationRequest(id));
    }

    public RecommendationRequestDto getRequest(RequestFilterDto filter) {
        var id = filter.getId();
        return getRequest(id);
    }

    private RecommendationRequest getRecommendationRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RecommendationRequest with id: " + id + " not found."));
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        var recommendationRequest = getRecommendationRequest(id);
        checkRequestStatus(recommendationRequest);

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getRejectionReason());
        var savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(savedRecommendationRequest);
    }

    private void checkRequestStatus(RecommendationRequest recommendationRequest) {
        if (!recommendationRequest.getStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalStateException("Only requests with PENDING status can be rejected.");
        }
    }
}
