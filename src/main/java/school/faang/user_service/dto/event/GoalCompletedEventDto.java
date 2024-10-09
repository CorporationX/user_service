package school.faang.user_service.dto.event;

import java.time.LocalDateTime;

public record GoalCompletedEventDto(
        long userId,
        long eventId,
        LocalDateTime timestamp) {
}