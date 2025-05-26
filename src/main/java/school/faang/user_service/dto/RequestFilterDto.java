package school.faang.user_service.dto;

import java.time.LocalDateTime;

public record RequestFilterDto(
        Long requesterId,
        Long receiverId,
        Long recommendationId,
        String messagePattern,
        LocalDateTime createdAfter,
        LocalDateTime createdBefore
) {
}