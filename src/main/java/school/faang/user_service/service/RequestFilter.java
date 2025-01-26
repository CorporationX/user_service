package school.faang.user_service.service;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RequestFilter {

    boolean isApplicable(RequestFilterDto requestFilterDto);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationStream,
                                        RequestFilterDto requestFilterDto);
}