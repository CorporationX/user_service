package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RecommendationRequestReceiverFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filter) {
        return filter != null && filter.getReceiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                               RecommendationRequestFilterDto filter) {
        return recommendationRequests.filter(recommendationRequest ->
                recommendationRequest.getReceiver().getId().equals(filter.getReceiverId()));
    }
}
