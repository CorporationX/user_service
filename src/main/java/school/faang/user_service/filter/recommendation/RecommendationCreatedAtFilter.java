package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import java.util.List;
import java.util.stream.Stream;

@Component
public class RecommendationCreatedAtFilter implements RecommendationRequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getCreatedAt() != null;
    }
    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RequestFilterDto filter) {
        return recommendationRequests.filter(request -> request.getCreatedAt().equals(filter.getCreatedAt()));
    }
}
