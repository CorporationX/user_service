package school.faang.user_service.service.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestMapper requestMapper;
    private final RecommendationRequestValidator requestValidator;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final List<RecommendationRequestFilter> recommendationsFilters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        requestValidator.validateRecommendationRequest(recommendationRequestDto);

        RecommendationRequest recommendationRequest = requestMapper.toEntity(recommendationRequestDto);
        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        saveSkillRequestsByNewRecommendation(recommendationRequestDto, savedRecommendationRequest);

        return requestMapper.toDto(savedRecommendationRequest);
    }

    private void saveSkillRequestsByNewRecommendation(RecommendationRequestDto recommendationRequestDto,
                                                      RecommendationRequest savedRecommendationRequest) {
        List<Long> skillRequests = recommendationRequestDto.skillsId();

        skillRequests
                .forEach(skillRequestId -> skillRequestRepository
                        .create(savedRecommendationRequest.getId(), skillRequestId));
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        Stream<RecommendationRequest> allRecommendationRequests = recommendationRequestRepository.findAll().stream();
        recommendationsFilters.stream()
                .filter(currentFilter -> currentFilter.isApplicable(filter))
                .forEach(currentFilter -> currentFilter.apply(allRecommendationRequests, filter));

        return requestMapper.toDto(allRecommendationRequests.toList());
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestRepository
                .findById(id)
                .map(requestMapper::toDto)
                .orElseThrow(() -> new DataValidationException(String.format("Request with id %s not found in database", id)));
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest requestToModify = recommendationRequestRepository
                .findById(id)
                .orElseThrow(() -> new DataValidationException("No such recommendation request with id " + id));

        if (requestToModify.getStatus() != RequestStatus.REJECTED) {
            requestToModify.setStatus(RequestStatus.REJECTED);
            requestToModify.setRejectionReason(rejection.getReason());
            recommendationRequestRepository.save(requestToModify);
        }

        return requestMapper.toDto(requestToModify);
    }
}
