package school.faang.user_service.filter.requestfilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.stream.Stream;

@Component
public class SkillRequestFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getSkillsPattern() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filters) {
        return recommendationRequestStream
                .filter(recommendationRequest -> recommendationRequest.getSkills().stream()
                        .map(SkillRequest::getId)
                        .toList()
                        .containsAll(filters.getSkillsPattern()));
    }
}
