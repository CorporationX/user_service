package school.faang.user_service.dto.event;

import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

@Getter
@Setter
public class EventFilterDto {
    private String title;
    private String description;
    private String location;
    private Integer maxAttendees;
    private Long ownerId;
    private EventType eventType;
    private EventStatus eventStatus;
}
