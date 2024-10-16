package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;
import school.faang.user_service.model.entity.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestReceiverFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto requestFilterDto) {
        return requestFilterDto.getReceiverIdPattern() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto requestFilterDto) {
        return recommendationRequests
                .filter(recommendationRequest ->
                        recommendationRequest.getReceiver().getId().equals(requestFilterDto.getReceiverIdPattern()));
    }
}
