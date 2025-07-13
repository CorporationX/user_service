package school.faang.user_service.kafka.producer;

import school.faang.user_service.kafka.Event;
import school.faang.user_service.kafka.events.AnalyticsEvent;

import java.util.List;

public interface DataSender {
    void send(KafkaTopics.Topic topic, Event event);

    void send(KafkaTopics.Topic topic, AnalyticsEvent analyticsEvent);

    void send(KafkaTopics.Topic topic, List<Long> ids);
}
