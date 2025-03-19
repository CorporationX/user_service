package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

@Data
public class EventFilterDto {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private EventType eventType;
    private EventStatus eventStatus;
}
