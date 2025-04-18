package school.faang.user_service.dto.event;


import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;


public record EventFilterDto(
        @Size(max = 50, message = "Size of title of event can't be exceed 50 characters")
        String title,
        LocalDateTime startDateBefore,
        LocalDateTime startDateAfter,
        String description,
        @Size(max = 50, message = "Size of location's title of event can't be exceed 50 characters")
        String location,
        EventType eventType
) {
}
