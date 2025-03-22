package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDto {

    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    private String description;
    private List<Long> relatedSkills;
    private String location;
    private int maxAttendees;
    private EventType eventType;
    private EventStatus eventStatus;
}
