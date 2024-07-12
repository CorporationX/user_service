package school.faang.user_service.service.filters;

import school.faang.user_service.controller.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.RecommendationRequestFilter;
import java.util.stream.Stream;

public class ReceiverNameFilter implements RecommendationRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getReceiverName()!=null &&
                !filterDto.getReceiverName().isEmpty();
    }

    @Override
    public void apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        requests.filter(recommendationRequest -> recommendationRequest.getReceiver().getUsername().matches(filterDto.getReceiverName()));
    }
}
