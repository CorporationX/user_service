package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
class ReceiverIdFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getReceiverId() != null && filters.getReceiverId() > 0;
    }

    @Override
    public Stream<RecommendationRequest> applyFilter(Stream<RecommendationRequest> requests, RequestFilterDto filters) {
        return requests.filter(request -> Objects.equals(request.getReceiver().getId(), filters.getReceiverId()));
    }
}
