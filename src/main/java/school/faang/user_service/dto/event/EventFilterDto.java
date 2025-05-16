package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Integer maxAttendees;
    private Long averageRate;
    private String eventType;
    private List<Long> skillsId;
    private Long ownerId;
    private String status;
}
