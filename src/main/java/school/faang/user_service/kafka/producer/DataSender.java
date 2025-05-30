package school.faang.user_service.kafka.producer;

import school.faang.user_service.kafka.AnalyticsEvent;

public interface DataSender {
    void send(String topic, String key, AnalyticsEvent analyticsEvent);
}
