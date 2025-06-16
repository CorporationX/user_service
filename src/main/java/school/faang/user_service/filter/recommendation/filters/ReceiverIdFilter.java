package school.faang.user_service.filter.recommendation.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ReceiverIdFilter implements RecommendationRequestFilterStrategy {

    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getReceiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RequestFilterDto requestFilterDto) {
        return recommendationRequests
                .filter(recommendationRequest ->
                        Objects.equals(recommendationRequest.getReceiver().getId(), requestFilterDto.getReceiverId()));
    }
}
