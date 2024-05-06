package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

@Data
public class EventFilterDto {
    private String description;
    private LocalDateTime startDate;
    private String location;
    private int maxAttendees;
    private EventType type;
    private EventStatus status;
}
