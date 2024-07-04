package school.faang.user_service.dto.event;

import lombok.Value;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class EventCreateEditDto  {

    String title;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Long ownerId;
    String description;
    List<Long> relatedSkillIds;
    String location;
    int maxAttendees;
    EventType eventType;
    EventStatus eventStatus;
}
