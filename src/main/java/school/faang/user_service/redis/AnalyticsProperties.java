package school.faang.user_service.redis;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.EventType;

import java.util.EnumSet;
import java.util.Set;

@Component
@Getter
public class AnalyticsProperties {
    @Value("${redis.analytics.counter-threshold}")
    private Integer counterThreshold;
    private final Set<EventType> allowed = EnumSet.of(EventType.PROFILE_VIEW, EventType.EVENT_VIEW);
}
