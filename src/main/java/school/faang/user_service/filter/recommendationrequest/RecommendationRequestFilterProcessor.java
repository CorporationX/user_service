package school.faang.user_service.filter.recommendationrequest;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RecommendationRequestFilterProcessor {

    private final List<RecommendationRequestFilter> filters = Arrays.asList(
            new RecommendationRequestRequesterFilter(),
            new RecommendationRequestReceiverFilter(),
            new RecommendationRequestStatusFilter(),
            new RecommendationRequestSkillsFilter(),
            new RecommendationRequestCreatedAtFilter(),
            new RecommendationRequestUpdatedAtFilter()
    );

    public Stream<RecommendationRequest> filter(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        return filters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(
                        requests,
                        (currentStream, filter) -> filter.apply(currentStream, filterDto),
                        (stream1, stream2) -> stream2
                );
    }
}