package school.faang.user_service.service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.util.filter.Filter;

import java.util.stream.Stream;

@Component
public class ReceiverNameFilter implements Filter<RequestFilterDto, RecommendationRequest> {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getReceiverName() != null &&
                !filterDto.getReceiverName().isEmpty();
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        return requests.filter(recommendationRequest -> recommendationRequest.getReceiver().getUsername().matches(filterDto.getReceiverName()));
    }
}
