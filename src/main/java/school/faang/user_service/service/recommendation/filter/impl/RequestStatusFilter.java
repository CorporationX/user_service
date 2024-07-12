package school.faang.user_service.service.recommendation.filter.impl;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.recommendation.filter.RequestFilter;

import java.util.List;
import java.util.stream.Stream;

public class RequestStatusFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(List<RecommendationRequest> requests, RequestFilterDto filter) {
        return requests.stream().filter(request -> request.getStatus().equals(filter.getStatus()));
    }
}
