package school.faang.user_service.filter.recommendationrequest;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RecommendationRequestRequesterFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getRequester() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        User requester = filterDto.getRequester();
        return requests.filter(request -> requester.equals(request.getRequester()));
    }
}