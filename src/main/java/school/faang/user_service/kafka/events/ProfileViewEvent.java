package school.faang.user_service.kafka.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import school.faang.user_service.kafka.Event;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public final class ProfileViewEvent extends Event {
    private AnalyticsEventType eventTypeEnum;
}
