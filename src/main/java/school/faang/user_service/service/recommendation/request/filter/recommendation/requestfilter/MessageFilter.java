package school.faang.user_service.service.recommendation.request.filter.recommendation.requestfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.recommendation.request.filter.Filter;

@Component
public class MessageFilter implements Filter<RecommendationRequestFilterDto, RecommendationRequestDto> {
    @Override
    public boolean applyFilter(RecommendationRequestDto data, RecommendationRequestFilterDto filter) {
        return data.getMessage().contains(filter.getMessage());
    }

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filter) {
        return filter.getMessage() != null;
    }
}
