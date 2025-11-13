package school.faang.user_service.filters.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class RecommendationReceiverFilter implements RecommendationFilter {
    @Override
    public Stream<Recommendation> apply(Stream<Recommendation> recommendations,
                                        FilterRecommendationRequestDto filters) {
        if (filters.receiverId() == null) {
            return recommendations;
        }
        return recommendations
                .filter(r -> Objects.equals(r.getReceiver().getId(), filters.receiverId()));
    }
}
