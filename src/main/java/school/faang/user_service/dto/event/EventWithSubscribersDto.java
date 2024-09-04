package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventWithSubscribersDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    private String description;
    private List<Long> relatedSkillsIds;
    private String location;
    private int maxAttendees;
    private int subscribersCount;
}
