package school.faang.user_service.EventOrganization.dto.event;

import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilterDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<SkillDto> relatedSkills;
    private String location;
    private int maxAttendees;
    private EventType type;
    private EventStatus status;
    private LocalDateTime createdAt;
}
