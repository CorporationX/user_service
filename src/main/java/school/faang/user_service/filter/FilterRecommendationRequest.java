package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface FilterRecommendationRequest {

    boolean isApplicable(RequestFilterDto requestFilterDto);

    void apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filterDto);

}
