package school.faang.user_service.filter.requestfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RequesterIdFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getRequesterIdPattern() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filters) {
        return recommendationRequestStream
                .filter(recommendationRequest -> recommendationRequest.getRequester().getId() == filters.getRequesterIdPattern());
    }
}
