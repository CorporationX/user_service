package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.RecommendationRequestFilter;

import java.util.stream.Stream;

@Component
public class RecommendationRequestFilterCreateAt implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getCreatedAt() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filterDto) {
        return recommendationRequestStream.filter(request -> request.getCreatedAt().isAfter(filterDto.getCreatedAt()));
    }
}
