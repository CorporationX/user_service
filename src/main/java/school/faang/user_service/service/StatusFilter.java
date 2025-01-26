package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class StatusFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatus() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationStream, RequestFilterDto requestFilterDto) {
        return recommendationStream.filter(request -> request.getStatus() == requestFilterDto.getStatus());
    }
}