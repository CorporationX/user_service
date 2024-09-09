package school.faang.user_service.service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;

@Component
public class RequestFilterMessage implements RequestFilter{

    @Override
    public boolean isApplicable(RequestFilterDto dto) {
        return dto.messagePattern() != null;
    }

    @Override
    public List<RecommendationRequest> apply(RequestFilterDto dto, List<RecommendationRequest> recommendationRequests) {
        return recommendationRequests.stream()
                .filter(request -> request.getMessage().contains(dto.messagePattern()))
                .toList();
    }
}
