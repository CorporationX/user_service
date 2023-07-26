package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation_request.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private static final String MSG = "There is no person with such id";

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest foundPerson = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    throw new IllegalStateException(MSG);
                });

        return recommendationRequestMapper.toDto(foundPerson);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RecommendationRequestFilterDto filterDto) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();

        for (RecommendationRequestFilter filter : recommendationRequestFilters) {
            if (filter.isApplicable(filterDto)) {
                recommendationRequests = filter.apply(recommendationRequests, filterDto);
            }
        }
        return recommendationRequestMapper.toDtoList(recommendationRequests.toList());
    }
}
