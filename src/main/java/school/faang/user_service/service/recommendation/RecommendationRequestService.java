package school.faang.user_service.service.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.dto.rejection.RejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        validate(recommendationRequestDto);
        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequestDto);

        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(entity));
    }

    public RecommendationRequestDto getRequest(long id) {
        Optional<RecommendationRequest> optionalRecommendationRequest = recommendationRequestRepository.findById(id);
        RecommendationRequest entity = optionalRecommendationRequest
                .orElseThrow(() -> new DataValidationException(MessageFormat.format("Recommendation with id = {0} does not exist!", id)));
        return recommendationRequestMapper.toDto(entity);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        Optional<RecommendationRequest> optionalRecommendationRequest = recommendationRequestRepository.findById(id);
        RecommendationRequest entity = optionalRecommendationRequest
                .orElseThrow(() -> new DataValidationException(MessageFormat.format("Recommendation with id = {0} does not exist!", id)));
        validatePendingStatus(entity);
        entity.setStatus(RequestStatus.REJECTED);
        entity.setRejectionReason(rejection.getReason());
        return recommendationRequestMapper.toDto(entity);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filterDto) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();
        return recommendationRequestFilters.stream()
                .filter(requestFilter -> requestFilter.isApplicable(filterDto))
                .flatMap(requestFilter -> requestFilter.apply(recommendationRequests, filterDto))
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    private void validate(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException(MessageFormat.format("User {0} does not exist!", userId));
        }
    }

    private void validate(List<Long> skillIds) {
        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId)) {
                throw new DataValidationException(MessageFormat.format("Skill {0}} does not exist!", skillId));
            }
        }
    }

    private void validate(LocalDateTime createdAt) {
        LocalDateTime nowTimeMinusSixMonths = LocalDateTime.now().minusMonths(6);
        if (!nowTimeMinusSixMonths.isAfter(createdAt)) {
            throw new DataValidationException("Recommendation can be requested once in six month!");
        }
    }

    private void validate(RecommendationRequestDto recommendationRequestDto) {
        validate(recommendationRequestDto.getRequesterId());
        validate(recommendationRequestDto.getReceiverId());
        validate(recommendationRequestDto.getSkillIds());
        validate(recommendationRequestDto.getCreatedAt());
    }

    private void validatePendingStatus(RecommendationRequest entity) {
        if (entity.getStatus().equals(RequestStatus.REJECTED)) {
            throw new DataValidationException("Recommendation request was rejected earlier!");
        }
        if (entity.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new DataValidationException("Recommendation request was accepted earlier!");
        }
    }
}
