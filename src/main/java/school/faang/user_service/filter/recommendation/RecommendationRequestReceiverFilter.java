package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestReceiverFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getReceiver() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        User receiver = filterDto.getReceiver();
        return requests.filter(request -> receiver.equals(request.getReceiver()));
    }
}