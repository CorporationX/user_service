package school.faang.user_service.service.redis.events;

import lombok.Data;
import school.faang.user_service.model.EventType;

import java.util.List;

@Data
public class EventInfo {

    private Long id;
    private String title;
    private EventType eventType;
    private List<Long> attendeesIds;
}
