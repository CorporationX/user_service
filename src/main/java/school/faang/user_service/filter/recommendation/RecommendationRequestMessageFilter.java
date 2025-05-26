package school.faang.user_service.filter.recommendation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestMessageFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filterDto) {
        return StringUtils.isNoneBlank(filterDto.getMessage());
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RecommendationRequestFilterDto filterDto) {
        return requests.filter(
                (request) -> request.getMessage().contains(filterDto.getMessage())
        );
    }
}
