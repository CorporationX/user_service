package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.FilterRecommendationRequest;

import java.util.stream.Stream;

@Component
public class FilterRecommendationRequestCreateAt implements FilterRecommendationRequest {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getCreatedAt() != null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filterDto) {
        recommendationRequestStream.filter(request -> request.getCreatedAt().equals(filterDto.getCreatedAt()));
    }
}
