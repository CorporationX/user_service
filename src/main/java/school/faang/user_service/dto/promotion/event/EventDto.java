package school.faang.user_service.dto.promotion.event;

import java.time.LocalDateTime;

public record EventDto(
        Long id,
        Long eventId,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String location
) {
}
