package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestStatusFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        RequestStatus status = filterDto.getStatus();
        return requests.filter(request -> status.equals(request.getStatus()));
    }
}