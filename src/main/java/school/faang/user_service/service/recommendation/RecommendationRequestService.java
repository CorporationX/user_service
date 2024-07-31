package school.faang.user_service.service.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.exeptions.NotFoundElement;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filter.RequestFilter;
import school.faang.user_service.validator.ValidatorForRecommendationRequestService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationRequestService {
    private static final String NOT_FOUND_REQUEST_RECOMMENDATIONS = "Not found RequestRecommendation for id: ";
    private static final String RECIPIENT_ID_NULL_OR_NEGATIVE = "recipientID apply in getRequest method was null or negative id: ";
    private final RecommendationRequestMapper requestMapper;
    private final RecommendationRequestRepository requestRepository;
    private final ValidatorForRecommendationRequestService recommendationServiceValidator;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RequestFilter> requestFilters;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto requestDto) {
        recommendationServiceValidator.validatorData(requestDto);
        requestDto.getSkillDtos().stream()
                .filter(skill -> !skillRequestRepository.existsById(skill.getSkillId()))
                .forEach(skill -> skillRequestRepository.create(requestDto.getId(), skill.getSkillId()));
        RecommendationRequest request = requestRepository.save(requestMapper.toEntity(requestDto));
        return requestMapper.toDto(request);
    }

    @Transactional(readOnly = true)
    public RecommendationRequestDto getRequest(@Positive long id) {
        var optionalRequest = requestRepository.findById(id);
        if (optionalRequest.isPresent()) {
            return requestMapper.toDto(optionalRequest.get());
        } else {
            log.error(NOT_FOUND_REQUEST_RECOMMENDATIONS + id);
            throw new NotFoundElement(NOT_FOUND_REQUEST_RECOMMENDATIONS + id);
        }
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(@Positive long id, String reason) {
        Optional<RecommendationRequest> request = requestRepository.findById(id);
        if (request.isPresent()) {
            var requestDto = requestMapper.toDto(request.get());
            if (requestDto.getStatus() == RequestStatus.PENDING) {
                requestDto.setStatus(RequestStatus.REJECTED);
                requestDto.setRejectionReason(reason);
                var recommendationRequest = requestRepository.save(requestMapper.toEntity(requestDto));
                return requestMapper.toDto(recommendationRequest);
            } else {
                return requestDto;
            }
        } else {
            log.error(NOT_FOUND_REQUEST_RECOMMENDATIONS + id);
            throw new NotFoundElement(NOT_FOUND_REQUEST_RECOMMENDATIONS + id);
        }
    }

    @Transactional(readOnly = true)
    public List<RecommendationRequestDto> getRequests(Long receiverId, RequestFilterDto filters) {
        if (receiverId == null || receiverId < 0) {
            log.error(RECIPIENT_ID_NULL_OR_NEGATIVE + receiverId);
            throw new DataValidationException(RECIPIENT_ID_NULL_OR_NEGATIVE + receiverId);
        }
        List<RecommendationRequest> allRequest = requestRepository.findAllRecommendationRequestForReceiver(receiverId);
        return requestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(allRequest, filters))
                .map(requestMapper::toDto)
                .toList();
    }
}
