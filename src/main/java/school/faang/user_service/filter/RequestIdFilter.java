package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RequestIdFilter implements RecommendationRequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RequestFilterDto filters) {
        return recommendationRequests
                .filter(recommendationRequest -> recommendationRequest.getId() == filters.getId());
    }
}
