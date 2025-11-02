package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UpdateEventDto {
    private String title;
    private String description;
    private Long ownerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private EventType eventType;
    private EventStatus status;
}
