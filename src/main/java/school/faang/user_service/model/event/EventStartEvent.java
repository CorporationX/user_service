package school.faang.user_service.model.event;

import school.faang.user_service.model.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStartEvent {
    private Long eventId;
    private List<Long> participantIds;
}
