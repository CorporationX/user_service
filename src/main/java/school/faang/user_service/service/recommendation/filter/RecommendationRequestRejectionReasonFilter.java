package school.faang.user_service.service.recommendation.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
class RecommendationRequestRejectionReasonFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filter) {
        return !StringUtils.isBlank(filter.getRejectionReason());
    }

    @Override
    public Stream<RecommendationRequest> apply(
            Stream<RecommendationRequest> requestStream, RecommendationRequestFilterDto filter
    ) {
        return requestStream.filter(
                recommendationRequest -> recommendationRequest.getRejectionReason().contains(filter.getRejectionReason())
        );
    }
}
