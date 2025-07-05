package school.faang.user_service.kafka.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class KafkaTopics {

    @Value("${spring.kafka.topics.analytics-created.name}")
    private String analyticsCreatedTopic;

    @Value("${spring.kafka.topics.analytics-profile-event-topic.name}")
    private String analyticsProfileEventTopic;

    @Value("${spring.kafka.topics.redis-retry-error-topic.name}")
    private String redisRetryErrorTopic;

    @Value("${spring.kafka.topics.follower-events.name}")
    private String followerEventsTopic;

    @Value("${spring.kafka.topics.follower-events.dlt.name}")
    private String followerEventsDltTopic;

    @Value("${spring.kafka.topics.profile-view-event-topic.name}")
    private String profileViewedTopic;

    @Value("${spring.kafka.topics.recommendation.name}")
    private String recommendationEventsTopic;

    @Value("${spring.kafka.topics.recommendation-request.name}")
    private String recommendationRequestTopic;
}
