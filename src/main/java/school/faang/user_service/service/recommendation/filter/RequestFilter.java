package school.faang.user_service.service.recommendation.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

public interface RequestFilter {

    boolean isApplicable(RequestFilterDto filter);

    Stream<RecommendationRequest> apply(List<RecommendationRequest> requests, RequestFilterDto filter);
}
