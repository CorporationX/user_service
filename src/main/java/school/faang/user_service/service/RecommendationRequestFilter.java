package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.controller.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import java.util.stream.Stream;
@Component
public interface RecommendationRequestFilter {
    boolean isApplicable(RequestFilterDto filterDto);
    void apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto);
}
