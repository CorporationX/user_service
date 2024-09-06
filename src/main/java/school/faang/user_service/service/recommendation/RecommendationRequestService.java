package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.recommendation.SkillRequestValidator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final SkillRequestValidator skillRequestValidator;
    private final List<RequestFilter> requestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestValidator.validateRequesterAndReceiverExists(recommendationRequestDto);
        recommendationRequestValidator.validatePreviousRequest(recommendationRequestDto);
        RecommendationRequest rq = recommendationRequestRepository.
                save(recommendationRequestMapper.toEntity(recommendationRequestDto));
        skillRequestValidator.validateSkillsExist((recommendationRequestDto.getSkills()));
        for (SkillDto skillDto : recommendationRequestDto.getSkills()) {
            skillRequestRepository.create(recommendationRequestDto.getId(), skillDto.getId());
        }
        return recommendationRequestMapper.toDto(rq);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();
        Stream<RecommendationRequest> filteredRequests = requestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.applyFilter(requests, filters));
        return filteredRequests
                .map(recommendationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper.toDto(recommendationRequestValidator.
                validateRecommendationRequestExists(id));
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest rq = recommendationRequestValidator.validateRequestStatusNotAcceptedOrDeclined(id);
        rq.setRejectionReason(rejection.getReason());
        rq.setStatus(RequestStatus.REJECTED);
        recommendationRequestRepository.save(rq);
        return recommendationRequestMapper.toDto(rq);
    }
}
