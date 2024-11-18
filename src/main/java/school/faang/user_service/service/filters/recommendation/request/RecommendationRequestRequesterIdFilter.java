package school.faang.user_service.service.filters.recommendation.request;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;
import java.util.stream.Stream;

public class RecommendationRequestRequesterIdFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getRequesterId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recRequests, RequestFilterDto filters) {
        return recRequests.filter(request -> filters.getRequesterId() == null || Objects
                .equals(request.getRequester().getId(), filters.getRequesterId()));
    }
}
