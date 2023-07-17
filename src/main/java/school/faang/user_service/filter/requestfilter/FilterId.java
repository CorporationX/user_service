package school.faang.user_service.filter.requestfilter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class FilterId implements RequestFilter{

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getId() > 0;
    }

    @Override
    public void apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filter) {
        recommendationRequestStream.filter(recommendationRequest -> recommendationRequest.getId() == filter.getId());
    }
}
