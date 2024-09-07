package school.faang.user_service.service.recomendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.FilterRecommendationRequestsDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestReceiverIdFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(FilterRecommendationRequestsDto filter) {
        return filter.getReceiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requestStream,
            FilterRecommendationRequestsDto filter
    ) {
        return requestStream.filter(
            recommendationRequest -> recommendationRequest.getReceiver().getId().equals(filter.getReceiverId())
        );
    }
}
