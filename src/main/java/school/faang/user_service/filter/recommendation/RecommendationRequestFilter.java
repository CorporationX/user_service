package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.entity.RecommendationRequest;
import school.faang.user_service.model.filter_dto.RecommendationRequestFilterDto;

import java.util.stream.Stream;

@Component
public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto requestFilterDto);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto requestFilterDto);
}
