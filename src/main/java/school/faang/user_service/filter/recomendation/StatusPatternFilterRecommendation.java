package school.faang.user_service.filter.recomendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class StatusPatternFilterRecommendation implements RecommendationRequestFilter {
    @Override
    public boolean isApplication(RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return recommendationRequestFilterDto.getStatusFilter() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requestStream, RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return requestStream.filter(request -> request.getStatus().equals(recommendationRequestFilterDto.getStatusFilter()));
    }
}
