package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.Objects;

@Component
public class ReceiverFilter implements RecommendationFilterStrategy {

    @Override
    public boolean isApplicable(RecommendationFilterDto filters) {
        return filters.receiverId() != null;
    }

    @Override
    public boolean matchesFilters(Recommendation recommendation, RecommendationFilterDto filters) {
        return Objects.equals(recommendation.getAuthor().getId(), filters.receiverId());
    }

}
