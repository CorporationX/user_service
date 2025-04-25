package school.faang.user_service.filter.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RecommendationRequestFilterProcessor {

    private final List<RecommendationRequestFilter> filters;

    public Stream<RecommendationRequest> filter(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(requests,
                        (currentStream, filter) -> filter.apply(currentStream, filterDto),
                        (stream1, stream2) -> stream2
                );
    }
}