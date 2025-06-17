package school.faang.user_service.kafka.producer;

import school.faang.user_service.kafka.events.AnalyticsEvent;
import school.faang.user_service.kafka.events.ProfileViewEvent;

import java.util.List;

public interface DataSender {
    void sendProfileViewEvent(String topic, ProfileViewEvent event);

    void send(String topic, AnalyticsEvent analyticsEvent);

    void send(String topic, List<Long> ids);
}
