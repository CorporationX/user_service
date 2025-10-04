package school.faang.user_service.dto.event;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

@Component
@Data
public class UpdateEventDto {
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private EventType eventType;
    private EventStatus eventStatus;
}
