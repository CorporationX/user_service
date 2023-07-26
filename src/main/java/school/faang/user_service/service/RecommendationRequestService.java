package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {

        List<RecommendationRequest> allRecommendationRequests = (List<RecommendationRequest>) recommendationRequestRepository.findAll();
        recommendationRequestFilters.stream().filter(requestFilter -> requestFilter.isApplicable(filter))
                .forEach(requestFilter -> requestFilter.apply(allRecommendationRequests.stream(), filter));

        return allRecommendationRequests.stream().map(recommendationRequestMapper::toDto).toList();
    }
}