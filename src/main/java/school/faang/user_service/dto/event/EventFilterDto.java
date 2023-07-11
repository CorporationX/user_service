package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class EventFilterDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    private List<Long> relatedSkillIds;
    private String location;
    private Integer maxAttendees;
}
