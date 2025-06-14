package school.faang.user_service.kafka.producer;

import school.faang.user_service.kafka.events.AnalyticsEvent;

import java.util.List;

public interface DataSender {
    void send(String topic, AnalyticsEvent analyticsEvent);

    void send(String topic, List<Long> ids);
}
