package school.faang.user_service.mapper.analytics;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.kafka.events.AnalyticsEvent;
import school.faang.user_service.kafka.events.AnalyticsEventType;

@Component
public class AnalyticsEventMapper {

    public AnalyticsEvent fromUser(User user, long id) {
        AnalyticsEvent analyticsEvent = new AnalyticsEvent();
        analyticsEvent.setReceiverId(user.getId());
        analyticsEvent.setActorId(id);
        analyticsEvent.setAnalyticsEventType(AnalyticsEventType.PROFILE_VIEW);
        return analyticsEvent;
    }

    public AnalyticsEvent fromEvent(Event event, long id) {
        AnalyticsEvent analyticsEvent = new AnalyticsEvent();
        analyticsEvent.setReceiverId(event.getId());
        analyticsEvent.setActorId(id);
        analyticsEvent.setAnalyticsEventType(AnalyticsEventType.EVENT_VIEW);
        return analyticsEvent;
    }
}
