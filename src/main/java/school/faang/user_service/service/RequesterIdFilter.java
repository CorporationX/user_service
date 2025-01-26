package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RequesterIdFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getRequesterId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationStream,
                                               RequestFilterDto requestFilterDto) {
        return recommendationStream.filter(request -> request
                .getRequester()
                .getId()
                .equals(requestFilterDto.getRequesterId()));
    }
}
