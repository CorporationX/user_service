package school.faang.user_service.dto.event;

import lombok.Value;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Value
public class WriteEventDto {

    String title;
    Instant startDate;
    LocalDateTime endDate;
    Long ownerId;
    String description;
    List<Long> relatedSkillIds;
    String location;
    int maxAttendees;
    EventType eventType;
    EventStatus eventStatus;
}
