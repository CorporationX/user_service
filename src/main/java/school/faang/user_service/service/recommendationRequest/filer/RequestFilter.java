package school.faang.user_service.service.recommendationRequest.filer;

import school.faang.user_service.dto.recommendationRequest.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RequestFilter {
    boolean isApplication(RequestFilterDto requestFilterDto);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requestStream, RequestFilterDto requestFilterDto);
}
