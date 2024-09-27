package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        Long receiverId = recommendationRequestDto.getReceiverId();
        Long requesterId = recommendationRequestDto.getRequesterId();

        recommendationRequestValidator.validateUserExistence(userRepository.existsById(receiverId), receiverId);
        recommendationRequestValidator.validateUserExistence(userRepository.existsById(requesterId), requesterId);

        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository
                .findLatestPendingRequest(requesterId, receiverId);
        LocalDateTime lastRequestTime = latestPendingRequest.map(RecommendationRequest::getUpdatedAt)
                .orElse(null);

        validations(requesterId, receiverId, lastRequestTime);

        RecommendationRequest recommendationRequestEntity = recommendationRequestMapper.toEntity(recommendationRequestDto);

        List<Long> skillIds = recommendationRequestEntity.getSkills().stream()
                .map(SkillRequest::getId)
                .toList();

        List<SkillRequest> existingSkills = skillRequestRepository.findAllById(skillIds);

        Set<Long> existingSkillIds = existingSkills.stream()
                .map(SkillRequest::getId)
                .collect(Collectors.toSet());

        recommendationRequestEntity.getSkills().stream()
                .filter(skill -> !existingSkillIds.contains(skill.getId()))
                .forEach(skill -> skillRequestRepository.create(recommendationRequestDto.getId(), skill.getId()));

        RecommendationRequest createRequest = recommendationRequestRepository.save(recommendationRequestEntity);

        return recommendationRequestMapper.toDto(createRequest);
    }

    private void validations(Long requesterId, Long receiverId, LocalDateTime lastRequestTime) {
        recommendationRequestValidator.validateRequesterAndReceiver(requesterId, receiverId);
        recommendationRequestValidator.validateRequestAndCheckTimeLimit(lastRequestTime);
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found RequestRecommendation for id: " + id));

        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    @Override
    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejectionDto) throws DataValidationException {
        RecommendationRequestDto recommendationRequestDto = getRequest(id);
        if (recommendationRequestDto.getStatus() == RequestStatus.PENDING) {
            RecommendationRequest recommendationRequestEntity = recommendationRequestMapper
                    .toEntity(recommendationRequestDto);
            recommendationRequestEntity.setStatus(RequestStatus.REJECTED);
            recommendationRequestEntity.setRejectionReason(rejectionDto.getReason());
            recommendationRequestRepository.save(recommendationRequestEntity);

            return recommendationRequestMapper.toDto(recommendationRequestEntity);
        } else {
            throw new DataValidationException("It is impossible to refuse a request that is not in a pending state");
        }
    }

    @Override
    @Transactional
    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto filters) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();

        return recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(recommendationRequests, filters))
                .map(recommendationRequestMapper::toDto)
                .toList();
    }
}
