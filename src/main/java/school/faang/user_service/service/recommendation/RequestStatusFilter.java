package school.faang.user_service.service.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RequestStatusFilter implements RequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> recommendationRequests, RequestFilterDto filter) {
        recommendationRequests
                .filter(recommendationRequest -> recommendationRequest.getStatus() == filter.getStatus());
    }
}
