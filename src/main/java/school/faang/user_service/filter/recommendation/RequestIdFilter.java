package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.recommendation.RequestFilterDto;
import school.faang.user_service.model.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RequestIdFilter implements RecommendationRequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.id() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RequestFilterDto filters) {
        return recommendationRequests
                .filter(recommendationRequest -> recommendationRequest.getId() == filters.id());
    }
}
