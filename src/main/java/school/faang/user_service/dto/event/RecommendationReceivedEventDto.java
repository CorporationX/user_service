package school.faang.user_service.dto.event;

import lombok.NonNull;

import java.time.LocalDateTime;

public record RecommendationReceivedEventDto(
        @NonNull Long id,
        @NonNull Long authorId,
        @NonNull Long receiverId,
        @NonNull LocalDateTime createdAt
) {
}
