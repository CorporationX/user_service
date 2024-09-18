package school.faang.user_service.service.recommendation.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto requestFilterDto);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto requestFilterDto);
}
