package school.faang.user_service.redis.promotion;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.events.AnalyticsEventType;

import java.util.EnumSet;
import java.util.Set;

@Component
public class PromotionAnalyticsProperties {
    @Getter
    @Value("${redis.analytics.counter-threshold}")
    private Integer counterThreshold;

    private final Set<AnalyticsEventType> allowed = EnumSet.of(AnalyticsEventType.PROFILE_VIEW, AnalyticsEventType.EVENT_VIEW);

    public Set<AnalyticsEventType> getAllowed() {
        return Set.of(allowed.toArray(new AnalyticsEventType[0]));
    }
}
