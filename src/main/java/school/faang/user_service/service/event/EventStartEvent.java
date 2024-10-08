package school.faang.user_service.service.event;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
public class EventStartEvent implements Serializable {
    private Long eventId;
    private List<Long> participantIds;

    public EventStartEvent(Long eventId, List<Long> participantIds) {
        this.eventId = eventId;
        this.participantIds = participantIds;
    }

}
