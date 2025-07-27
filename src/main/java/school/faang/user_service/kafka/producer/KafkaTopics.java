package school.faang.user_service.kafka.producer;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka.topics")
@Data
public class KafkaTopics {

    private Topic analyticsCreated;
    private Topic analyticsProfileEventTopic;
    private Topic redisRetryErrorTopic;
    private Topic profileViewEventTopic;
    private Topic recommendationRequestTopic;
    private Topic premiumBoughtTopic;
    private Topic recommendationEventsTopic;
    private Topic followerEvents;
    private Topic profilePicEventTopic;

    @Data
    public static class Topic {
        private final String name;
        private final int partitions;
        private final int replicationFactor;
        private final Dlt dlt;
    }

    @Data
    public static class Dlt{
        private final String name;
        private final int partitions;
        private final int replicationFactor;
    }
}
