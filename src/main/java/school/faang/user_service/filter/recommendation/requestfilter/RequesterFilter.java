package school.faang.user_service.filter.recommendation.requestfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.filter.Filter;

@Component
public class RequesterFilter implements Filter<RecommendationRequestFilterDto, RecommendationRequestDto> {

    @Override
    public boolean applyFilter(RecommendationRequestDto data, RecommendationRequestFilterDto filter) {
        return data.getRequesterId().equals(filter.getRequesterId());
    }

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filter) {
        return filter.getRequesterId() != null;
    }
}
