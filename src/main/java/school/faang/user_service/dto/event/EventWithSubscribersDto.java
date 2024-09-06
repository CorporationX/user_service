package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class EventWithSubscribersDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    private String description;
    private List<Long> relatedSkillsIds;
    private String location;
    private Integer maxAttendees;
    private Integer subscribersCount;
    private EventType type;
    private EventStatus status;
}
