package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.controller.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import java.util.stream.Stream;

public interface RecommendationRequestFilter {
    boolean isApplicable(RequestFilterDto filterDto);
    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto);
}
