package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventStartEvent {

    private long eventId;
    private String eventName;
    private Instant startDate;
    private EventDelayTime eventDelayTime;
}