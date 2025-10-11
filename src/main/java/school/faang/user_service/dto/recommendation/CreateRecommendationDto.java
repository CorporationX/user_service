package school.faang.user_service.dto.recommendation;

import java.util.List;

public record CreateRecommendationDto(
        Long receiverId,
        String content,
        List<Long> skillIds
) {
}
