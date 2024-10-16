package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;
import school.faang.user_service.model.entity.RecommendationRequest;

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
