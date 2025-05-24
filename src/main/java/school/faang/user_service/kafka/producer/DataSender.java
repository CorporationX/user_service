package school.faang.user_service.kafka.producer;

import school.faang.user_service.kafka.AnalyticsCreatedEvent;

public interface DataSender {
    void send(String topic, String key, AnalyticsCreatedEvent analyticsCreatedEvent);
}
