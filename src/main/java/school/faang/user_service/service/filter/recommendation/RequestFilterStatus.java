package school.faang.user_service.service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class RequestFilterStatus implements RequestFilter{

    @Override
    public boolean isApplicable(RequestFilterDto dto) {
        return dto.status() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(RequestFilterDto dto, Stream<RecommendationRequest> recommendationRequests) {
        return recommendationRequests
                .filter(request -> request.getStatus() == dto.status());
    }
}
