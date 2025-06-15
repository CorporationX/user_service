package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class EventFilterDto {
    private String title;
    private LocalDateTime startDateFrom;
    private LocalDateTime startDateTo;
    private EventType eventType;
    private EventStatus eventStatus;
    private Long ownerId;
    private List<Long> relatedSkills;
    private String location;
}
