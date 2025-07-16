package school.faang.user_service.dto.recommendation;

import jakarta.annotation.Nullable;
import school.faang.user_service.entity.RequestStatus;

public record RecommendationRequestFilterDto(
        @Nullable
        Long requesterId,
        @Nullable
        Long receiverId,
        @Nullable
        String messageContains,
        @Nullable
        RequestStatus status
) {
}
