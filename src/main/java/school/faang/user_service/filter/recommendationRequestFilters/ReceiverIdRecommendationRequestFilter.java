package school.faang.user_service.filter.recommendationRequestFilters;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.model.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class ReceiverIdRecommendationRequestFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.receiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        return requests.filter(recommendationRequest ->
                recommendationRequest.getReceiver().getId().equals(filterDto.receiverId()));
    }
}
