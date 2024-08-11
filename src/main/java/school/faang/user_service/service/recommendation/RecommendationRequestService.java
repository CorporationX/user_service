package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filter.RequestFilter;
import school.faang.user_service.validator.ValidatorForRecommendationRequest;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationRequestService {
    private static final String NOT_FOUND_REQUEST_RECOMMENDATIONS = "Not found RequestRecommendation for id: ";
    private final RecommendationRequestMapper requestMapper;
    private final RecommendationRequestRepository requestRepository;
    private final ValidatorForRecommendationRequest recommendationRequestValidator;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RequestFilter> requestFilters;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto requestDto) {
        recommendationRequestValidator.validate(requestDto);
        recommendationRequestValidator.validaRecommendationRequestByIdAndUpdateAt(requestDto);
        RecommendationRequest request = requestMapper.toEntity(requestDto);
        request.getSkills().stream()
                .filter(skill -> !skillRequestRepository.existsById(skill.getId()))
                .forEach(skill -> skillRequestRepository.create(requestDto.getId(), skill.getId()));
        RecommendationRequest createRequest = requestRepository.save(request);
        return requestMapper.toDto(createRequest);
    }

    @Transactional(readOnly = true)
    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = requestRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(NOT_FOUND_REQUEST_RECOMMENDATIONS + id));
        return requestMapper.toDto(request);
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, String reason) {
        RecommendationRequestDto requestDto = getRequest(id);
        recommendationRequestValidator.validaRecommendationRequestByIdAndUpdateAt(requestDto);
        if (requestDto.getStatus() == RequestStatus.PENDING) {
            RecommendationRequest request = requestMapper.toEntity(requestDto);
            request.setStatus(RequestStatus.REJECTED);
            request.setRejectionReason(reason);
            return requestMapper.toDto(request);
        } else {
            String messageError = "It is impossible to refuse a request that is not in a pending state";
            log.info(messageError);
            throw new DataValidationException(messageError);
        }
    }

    @Transactional(readOnly = true)
    public List<RecommendationRequestDto> getFilteredRequests(Long receiverId, RequestFilterDto filters) {
        List<RecommendationRequest> allRequest =
                requestRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);
        return requestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(allRequest, filters))
                .map(requestMapper::toDto)
                .toList();
    }
}
