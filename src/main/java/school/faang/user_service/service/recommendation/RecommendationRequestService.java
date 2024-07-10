package school.faang.user_service.service.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
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
    private final RecommendationRequestMapper requestMapper;
    private final RecommendationRequestRepository requestRepository;
    private final ValidatorForRecommendationRequestService validator;


    public RecommendationRequestDto create(RecommendationRequestDto requestDto) {
        validator.validatorData(requestDto);
        RecommendationRequest request = requestRepository.save(requestMapper.toEntity(requestDto));
        return requestMapper.toDto(request);
    }

    public RecommendationRequestDto getRequest(@Positive long id) {
        var optionalRequest = requestRepository.findById(id);
        if (optionalRequest.isPresent()) {
            return requestMapper.toDto(optionalRequest.get());
        } else {
            log.error("Not found RequestRecommendation for id" + id);
            throw new NoSuchElementException("Not found RequestRecommendation for id" + id);
        }
    }

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
            log.error("Could not find requestRecommendation for id: " + id);
            throw new NoSuchElementException("Could not find requestRecommendation for id: " + id);
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
                .map(request -> requestMapper.toDto(request))
                .toList();
        if (listForFilteredRecommendation.isEmpty()) {
            log.info(String.format(
                    "not found RequestRecommendation for filters skill: %s, status: %s", filter.getSkillId(), filter.getStatus()));
            throw new NoSuchElementException(String.format(
                    "not found RequestRecommendation for filters skill: %s, status: %s", filter.getSkillId(), filter.getStatus()));
        }
        return listForFilteredRecommendation;
    }
}
