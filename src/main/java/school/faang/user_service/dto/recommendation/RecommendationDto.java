package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationDto(
        Long id,
        Long authorId,
        String authorUsername,
        Long receiverId,
        String receiverUsername,
        String content,
        List<Long> skillIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
