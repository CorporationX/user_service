package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestFilterReceiverId implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return recommendationRequestFilterDto.receiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequestStream,
                                               RecommendationRequestFilterDto recommendationRequestFilterDto) {
        return recommendationRequestStream
                .filter(recommendationRequest ->
                        recommendationRequestFilterDto.receiverId()
                                .equals(recommendationRequest.getReceiver().getId()));

    }
}
