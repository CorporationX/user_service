package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestCreatedAfterFilter implements RecommendationRequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getCreatedAfter() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests,
                                               RequestFilterDto requestFilterDto) {
        return recommendationRequests.filter(recommendationRequest -> recommendationRequest.getCreatedAt()
                .isAfter(requestFilterDto.getCreatedAfter()));
    }
}
