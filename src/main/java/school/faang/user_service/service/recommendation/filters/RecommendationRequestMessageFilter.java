package school.faang.user_service.service.recommendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestMessageFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestFilterDto) {
        return requestFilterDto.getMessagePattern() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto requestFilterDto) {
        return recommendationRequests
                .filter(recommendationRequest -> recommendationRequest.getMessage().contains(requestFilterDto.getMessagePattern()));
    }
}
