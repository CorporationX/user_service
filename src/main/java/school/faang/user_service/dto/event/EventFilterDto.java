package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.entity.event.EventType;

@Data
public class EventFilterDto {
    private String titleContains;
    private String descriptionContains;
    private long ownerId;
    private long participantId;
    private EventType eventType;
}
