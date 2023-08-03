package school.faang.user_service.service.reccomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.reccomendation.filter.RecommendationRequestFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository repository;
    private final RecommendationRequestMapper mapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public List<RecommendationRequestDto> getRequest(RequestFilterDto filters) {
        Stream<RecommendationRequest> recommendationRequests = repository.findAll().stream();
        recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(recommendationRequests, filters));
        return recommendationRequests
                .map(mapper::toDto)
                .toList();
    }
}
