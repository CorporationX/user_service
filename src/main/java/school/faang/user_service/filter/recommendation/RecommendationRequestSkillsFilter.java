package school.faang.user_service.filter.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@Component
public class RecommendationRequestSkillsFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getSkills() != null && !filterDto.getSkills().isEmpty();
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requests, RequestFilterDto filterDto) {
        List<SkillRequestDto> requiredSkills = filterDto.getSkills();
        return requests.filter(request -> {
            List<?> skills = request.getSkills();
            if (skills == null || skills.isEmpty()) {
                return false;
            }
            return new HashSet<>(skills).containsAll(requiredSkills);
        });
    }
}