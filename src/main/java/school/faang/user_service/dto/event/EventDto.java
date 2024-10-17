package school.faang.user_service.dto.event;

import java.time.LocalDateTime;
import java.util.List;

public record EventDto(
        List<Long> userIds,
        long eventId,
        String title,
        LocalDateTime startDateTime) {
}