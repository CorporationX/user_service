package school.faang.user_service.filter.recommendation;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class RecommendationRequestUpdatedAtFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getUpdatedAt() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        LocalDateTime threshold = filterDto.getUpdatedAt();
        return requests.filter(request -> {
            LocalDateTime updatedAt = request.getUpdatedAt();
            if (updatedAt == null) {
                return false;
            }
            return !updatedAt.isBefore(threshold);
        });
    }
}