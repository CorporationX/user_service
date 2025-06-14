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
    @Required
    private LocalDateTime endDate;
    @Required
    private Long ownerId;
    @Required
    private String description;
    @Required
    private List<Long> relatedSkillsIds;
    @Required
    private String location;
    private Integer maxAttendees;
    @Required
    private String type;
}
