package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
class RequesterIdFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filters) {
        return filters.getRequesterId() != null && filters.getRequesterId() > 0;
    }

    @Override
    public Stream<RecommendationRequest> applyFilter(Stream<RecommendationRequest> requests, RecommendationRequestFilterDto filters) {
        return requests.filter(request -> Objects.equals(request.getRequester().getId(), filters.getRequesterId()));
    }
}
