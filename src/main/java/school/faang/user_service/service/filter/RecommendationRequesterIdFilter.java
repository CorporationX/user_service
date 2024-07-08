package school.faang.user_service.service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public class RecommendationRequesterIdFilter implements  RecommendationFilter<RecommendationRequest, RequestFilterDto>{
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getRequesterId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto recommendationFilterDto) {
        return recommendationRequestStream.filter(recommendationRequest -> recommendationRequest.getRequester().getId() == recommendationFilterDto.getRequesterId());
    }


}
