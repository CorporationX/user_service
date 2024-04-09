package school.faang.user_service.filter.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.RecommendationRequestFilter;
import school.faang.user_service.mapper.SkillRequestMapper;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RecommendationRequestFilterSkills implements RecommendationRequestFilter {

    private final SkillRequestMapper skillRequestMapper;

    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getSkills() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequestStream, RequestFilterDto filterDto) {
        List<SkillRequest> list = skillRequestMapper.toEntity(filterDto.getSkills());
        return recommendationRequestStream.filter(request -> request.getSkills().containsAll(list));
    }
}
