package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

@Data
@Builder
public class EventFilterDto {
    private String titlePattern;
    private LocalDateTime startDatePattern;
    private LocalDateTime endDatePattern;
    private String locationPattern;
    private String ownerPattern;
    private EventType eventTypePattern;
    private EventStatus eventStatusPattern;
}
