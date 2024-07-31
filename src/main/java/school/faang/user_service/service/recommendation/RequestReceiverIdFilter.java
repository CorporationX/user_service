package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RequestReceiverIdFilter implements RequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getReceiverId() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> recommendationRequests, RequestFilterDto filter) {
        recommendationRequests
                .filter(recommendationRequest -> recommendationRequest.getReceiver().getId() == filter.getReceiverId());
    }
}
