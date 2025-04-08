package school.faang.user_service.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class StatusFilter implements RequestFilter {
    private final RecommendationRequestMapper recommendationRequestMapper;

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filter) {
        RequestStatus status = recommendationRequestMapper.mapStatusToEntity(filter.getStatus());
        return requests.filter(request -> request.getStatus().equals(status));
    }
}
