package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.requestfilter.RequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;

    private final RecommendationRequestValidator recommendationRequestValidator;

    private final SkillValidator skillValidator;

    private List<RequestFilter> requestFilters;

    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestValidator.validationExistById(recommendationRequestDto.getRequesterId());
        recommendationRequestValidator.validationExistById(recommendationRequestDto.getReceiverId());
        recommendationRequestValidator.validationRequestDate(recommendationRequestDto);
        skillValidator.validationExistSkill(recommendationRequestDto);
        RecommendationRequest entity = recommendationRequestMapper.toEntity(recommendationRequestDto);
        return recommendationRequestMapper.toDto(recommendationRequestRepository.save(entity));
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        Stream<RecommendationRequest> requestStream = StreamSupport.stream(recommendationRequestRepository
                .findAll().spliterator(), false);

        List<RequestFilter> requestFilterStream = requestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        for (RequestFilter filter : requestFilterStream) {
            requestStream = filter.apply(requestStream, filters);
        }
        return requestStream
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    @Autowired
    public void setRequestFilters(List<RequestFilter> requestFilters) {
        this.requestFilters = requestFilters;
    }
}
