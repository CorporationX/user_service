package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.validation.data.Required;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDto {
    private Long id;
    @Required
    private String title;
    @Required
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Required
    private Long ownerId;
    private String description;
    private List<Long> relatedSkillsIds;
    private String location;
    private Integer maxAttendees;
    private String eventType;
}
