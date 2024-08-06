package school.faang.user_service.service.recommendation.request.filter.recommendation.requestfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.service.recommendation.request.filter.Filter;

@Component("recommendationStatusFilter")
public class StatusFilter implements Filter<RecommendationRequestFilter, RecommendationRequestDto> {
    @Override
    public boolean applyFilter(RecommendationRequestDto data, RecommendationRequestFilter filter) {
        return data.getStatus().equals(filter.getStatus());
    }

    @Override
    public boolean isApplicable(RecommendationRequestFilter filter) {
        return filter.getStatus() != null;
    }
}
