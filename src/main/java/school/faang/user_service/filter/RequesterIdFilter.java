package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RequesterIdFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getRequesterId() != null && filter.getRequesterId() > 0;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filter) {
        return requests.filter(request -> request.getRequester().getId().equals(filter.getRequesterId()));
    }
}