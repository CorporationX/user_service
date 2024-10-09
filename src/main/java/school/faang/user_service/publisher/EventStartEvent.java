package school.faang.user_service.publisher;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventStartEvent implements Serializable {
    private Long eventId;
    private List<Long> participantIds;

    public EventStartEvent(Long eventId, List<Long> participantIds) {
        this.eventId = eventId;
        this.participantIds = participantIds;
    }
}