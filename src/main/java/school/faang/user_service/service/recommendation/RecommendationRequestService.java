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
import school.faang.user_service.exeptions.NotFoundElement;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.ValidatorForRecommendationRequestService;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendationRequestService {
    private static final String NOT_FOUND_REQUEST_RECOMMENDATIONS = "Not found RequestRecommendation for id: ";
    private static final String NOT_FOUND_REQUEST_RECOMMENDATIONS_FOR_FILTER
            = "Not found RequestRecommendation for filters skill: %s, status: %s";
    private final RecommendationRequestMapper requestMapper;
    private final RecommendationRequestRepository requestRepository;
    private final ValidatorForRecommendationRequestService validator;
    private final SkillRequestRepository skillRequestRepository;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto requestDto) {
        validator.validatorData(requestDto);
        saveSkillRequest(requestDto);
        RecommendationRequest request = requestRepository.save(requestMapper.toEntityList(requestDto));
        return requestMapper.toDtoList(request);
    }

    @Transactional(readOnly = true)
    public RecommendationRequestDto getRequest(@Positive long id) {
        var optionalRequest = requestRepository.findById(id);
        if (optionalRequest.isPresent()) {
            return requestMapper.toDtoList(optionalRequest.get());
        } else {
            log.error(NOT_FOUND_REQUEST_RECOMMENDATIONS + "{}", id);
            throw new NotFoundElement(NOT_FOUND_REQUEST_RECOMMENDATIONS + id);
        }
    }

    public RecommendationRequestDto rejectRequest(@Positive long id, String reason) {
        Optional<RecommendationRequest> request = requestRepository.findById(id);
        if (request.isPresent()) {
            var requestDto = requestMapper.toDtoList(request.get());
            if (requestDto.getStatus() == RequestStatus.PENDING) {
                requestDto.setStatus(RequestStatus.REJECTED);
                requestDto.setRejectionReason(reason);
                var recommendationRequest = requestRepository.save(requestMapper.toEntityList(requestDto));
                return requestMapper.toDtoList(recommendationRequest);
            } else {
                return requestDto;
            }
        } else {
            log.error(NOT_FOUND_REQUEST_RECOMMENDATIONS + "{}", id);
            throw new NotFoundElement(NOT_FOUND_REQUEST_RECOMMENDATIONS + id);
        }
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        Set<RecommendationRequest> allRequest = new HashSet<>(requestRepository.findAll());
        var listForFilteredRecommendation = allRequest.stream()
                .filter(request -> {
                    if (filter.getSkillId() != null) {
                        return request.getSkills().contains(filter.getSkillId());
                    }
                    return true;
                })
                .filter(request -> {
                    if (filter.getStatus() != null) {
                        return request.getStatus().equals(filter.getStatus());
                    }
                    return true;
                })
                .map(request -> requestMapper.toDtoList(request))
                .toList();
        if (listForFilteredRecommendation.isEmpty()) {
            log.info(
                    String.format(NOT_FOUND_REQUEST_RECOMMENDATIONS_FOR_FILTER, filter.getSkillId(), filter.getStatus()));
            throw new NoSuchElementException(
                    String.format(NOT_FOUND_REQUEST_RECOMMENDATIONS_FOR_FILTER, filter.getSkillId(), filter.getStatus()));
        }
        return listForFilteredRecommendation;
    }

    private void saveSkillRequest(RecommendationRequestDto requestDto) {
        requestDto.getSkills().forEach(
                skill -> skillRequestRepository.create(requestDto.getId(), skill.getSkillId()));
    }
}
