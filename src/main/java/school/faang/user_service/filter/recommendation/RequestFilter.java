package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public interface RequestFilter {

    boolean isApplicable(RecommendationRequestFilterDto filters);

    Stream<RecommendationRequest> applyFilter(Stream<RecommendationRequest> requests, RecommendationRequestFilterDto filters);
}
