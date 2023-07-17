package school.faang.user_service.filter.requestfilter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RequestFilter {

    boolean isApplicable(RequestFilterDto filter);

    void apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filter);
}
