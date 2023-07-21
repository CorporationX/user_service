package school.faang.user_service.filter.requestfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class MessageFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getMessagePattern() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filters) {
        return recommendationRequestStream
                .filter(recommendationRequest -> {
                    if (recommendationRequest.getMessage() == null) {
                        return false;
                    } else {
                        return recommendationRequest.getMessage().contains(filters.getMessagePattern());
                    }
                });
    }
}
