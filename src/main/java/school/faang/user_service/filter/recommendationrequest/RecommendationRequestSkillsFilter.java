package school.faang.user_service.filter.recommendationrequest;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;
import java.util.stream.Stream;

public class RecommendationRequestSkillsFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getSkills() != null && !filterDto.getSkills().isEmpty();
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        List<SkillRequest> requiredSkills = filterDto.getSkills();
        return requests.filter(request -> {
            List<?> skills = request.getSkills();
            if (skills == null || skills.isEmpty()) {
                return false;
            }
            return skills.containsAll(requiredSkills);
        });
    }
}