package school.faang.user_service.filter.recomendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendationRequest.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recomendation.RequestFilter;

import java.util.stream.Stream;

@Component
public class StatusPatternFilter implements RequestFilter {
    @Override
    public boolean isApplication(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatusFilter() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requestStream, RequestFilterDto requestFilterDto) {
        return requestStream.filter(request -> request.getStatus().equals(requestFilterDto.getStatusFilter()));
    }
}
