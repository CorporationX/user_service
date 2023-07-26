package school.faang.user_service.service.reccomendation.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestCreatedAtFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getCreatedAt() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filters) {
        return requests
                .filter(request -> request.getCreatedAt().isEqual(filters.getCreatedAt()));
    }
}
