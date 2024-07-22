package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Component
public class RecommendationFilter {

    public boolean matchesFilter(RecommendationRequest recommendationRequest, RequestFilterDto filter) {
        if (filter != null && filter.getStatus() != null
                && !filter.getStatus().toUpperCase().equals(recommendationRequest.getStatus().name())) return false;
        if (filter != null
                && filter.getRequesterId() != null && !filter.getRequesterId().equals(recommendationRequest.getRequester().getId())) return false;
        return filter == null || filter.getReceiverId() == null || filter.getReceiverId().equals(recommendationRequest.getReceiver().getId());
    }
}
