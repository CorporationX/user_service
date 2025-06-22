package school.faang.user_service.kafka.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import school.faang.user_service.kafka.Event;

@Data
@EqualsAndHashCode(callSuper = true)
public final class ProfileViewEvent extends Event {
    private AnalyticsEventType eventTypeEnum;
}
