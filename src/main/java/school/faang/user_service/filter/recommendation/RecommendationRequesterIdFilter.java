package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import java.util.List;
import java.util.stream.Stream;

@Component
public class RecommendationRequesterIdFilter implements RecommendationRequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getRequesterId() != null;
    }
    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RequestFilterDto filter) {
        return recommendationRequests.filter(request -> request.getRequester().getId() == filter.getRequesterId());
    }
}
