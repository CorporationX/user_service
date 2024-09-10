package school.faang.user_service.EventOrganization.dto.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDto {
    private Long id;
    String title;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Long ownerId;
    String description;
    List<SkillDto> relatedSkills;
    String location;
    int maxAttendees;
}
