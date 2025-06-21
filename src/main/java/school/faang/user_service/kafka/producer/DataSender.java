package school.faang.user_service.kafka.producer;

import school.faang.user_service.kafka.Event;
import school.faang.user_service.kafka.events.AnalyticsEvent;
import school.faang.user_service.kafka.events.RecommendationEvent;

import java.util.List;

public interface DataSender {
    void send(String topic, Event event);

    void send(String topic, AnalyticsEvent analyticsEvent);

    void send(String topic, List<Long> ids);

    void send(String topic, RecommendationEvent recommendationEvent);
}
