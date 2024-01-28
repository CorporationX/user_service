package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.FilterRecommendationRequest;

import java.util.stream.Stream;

@Component
public class FilterRecommendationRequestSkills implements FilterRecommendationRequest {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getSkills()!= null;
    }

    @Override
    public void apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filterDto) {
        recommendationRequestStream.filter(request -> request.getSkills().equals(filterDto.getSkills()));
    }
}
