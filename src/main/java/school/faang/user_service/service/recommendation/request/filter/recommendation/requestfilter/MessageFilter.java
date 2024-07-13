package school.faang.user_service.service.recommendation.request.filter.recommendation.requestfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.service.recommendation.request.filter.Filter;

@Component
public class MessageFilter implements Filter<RecommendationRequestFilter, RecommendationRequestDto> {
    @Override
    public boolean applyFilter(RecommendationRequestDto data, RecommendationRequestFilter filter) {
        return data.getMessage().contains(filter.getMessage());
    }

    @Override
    public boolean isApplicable(RecommendationRequestFilter filter) {
        return filter.getMessage() != null;
    }
}
