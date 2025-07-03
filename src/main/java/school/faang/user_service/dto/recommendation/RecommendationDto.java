package school.faang.user_service.dto.recommendation;

import java.time.LocalDateTime;

public record RecommendationDto(
        Long id,
        Long authorId,
        Long receiverId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
