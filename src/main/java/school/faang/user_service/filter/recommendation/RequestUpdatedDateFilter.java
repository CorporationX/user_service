package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
class RequestUpdatedDateFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filters) {
        return filters.getUpdatedAt() != null;
    }

    @Override
    public Stream<RecommendationRequest> applyFilter(Stream<RecommendationRequest> requests, RecommendationRequestFilterDto filters) {
        return requests.filter(request -> request.getUpdatedAt().toLocalDate()
                .isEqual(filters.getUpdatedAt()));
    }
}
