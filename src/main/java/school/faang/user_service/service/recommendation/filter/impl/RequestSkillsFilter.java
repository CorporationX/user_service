package school.faang.user_service.service.recommendation.filter.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.recommendation.filter.RequestFilter;

import java.util.List;
import java.util.stream.Stream;

@Component
public class RequestSkillsFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getSkillRequestDtos() != null && !filter.getSkillRequestDtos().isEmpty();
    }

    @Override
    public Stream<RecommendationRequest> apply(List<RecommendationRequest> requests, RequestFilterDto filter) {
        return requests.stream().filter(request -> request.getSkills().containsAll(filter.getSkillRequestDtos()));
    }
}
