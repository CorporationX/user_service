package school.faang.user_service.dto.recommendation;

import java.util.List;

public record UpdateRecommendationDto(
        String content,
        List<Long> skillIds
) {
}
