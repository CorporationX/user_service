package school.faang.user_service.service.filters.recommendation.request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class RecommendationRequestIdFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recRequests, RequestFilterDto filters) {
        return recRequests.filter(request -> filters.getId() == null ||
                Objects.equals(request.getId(), filters.getId()));
    }
}
