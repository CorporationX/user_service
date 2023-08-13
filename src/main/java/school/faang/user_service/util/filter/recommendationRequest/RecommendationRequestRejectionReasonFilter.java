package school.faang.user_service.util.filter.recommendationRequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestRejectionReasonFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getRejectionReason() != null && !filters.getRejectionReason().isBlank();
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filters) {
        return requests
                .filter(request -> request.getRejectionReason().contains(filters.getRejectionReason().trim()));
    }
}
