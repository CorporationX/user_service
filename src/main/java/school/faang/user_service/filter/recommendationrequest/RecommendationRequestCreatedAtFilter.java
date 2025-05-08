package school.faang.user_service.filter.recommendationrequest;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class RecommendationRequestCreatedAtFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getCreatedAt() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        LocalDateTime threshold = filterDto.getCreatedAt();
        return requests.filter(request -> {
            LocalDateTime createdAt = request.getCreatedAt();
            if (createdAt == null) {
                return false;
            }
            return !createdAt.isBefore(threshold);
        });
    }
}