package school.faang.user_service.kafka.producer;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicsConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setAutoCreate(true);
        return admin;
    }

    /* ---- analytics-created ---- */

    @Bean
    public NewTopic analyticsCreatedTopic(
            @Value("${spring.kafka.topics.analytics-created.name}") String name,
            @Value("${spring.kafka.topics.analytics-created.partitions}") int partitions,
            @Value("${spring.kafka.topics.analytics-created.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic analyticsCreatedDlt(
            @Value("${spring.kafka.topics.analytics-created.dlt.name}") String name,
            @Value("${spring.kafka.topics.analytics-created.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.analytics-created.dlt.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    /* ---- logs-user-service ---- */

    @Bean
    public NewTopic logsUserServiceTopic(
            @Value("${spring.kafka.topics.logs-user-service.name}") String name,
            @Value("${spring.kafka.topics.logs-user-service.partitions}") int partitions,
            @Value("${spring.kafka.topics.logs-user-service.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic logsUserServiceDlt(
            @Value("${spring.kafka.topics.logs-user-service.dlt.name}") String name,
            @Value("${spring.kafka.topics.logs-user-service.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.logs-user-service.dlt.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    /* ---- analytics-profile-event ---- */

    @Bean
    public NewTopic analyticsProfileEventTopic(
            @Value("${spring.kafka.topics.analytics-profile-event-topic.name}") String name,
            @Value("${spring.kafka.topics.analytics-profile-event-topic.partitions}") int partitions,
            @Value("${spring.kafka.topics.analytics-profile-event-topic.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic analyticsProfileEventDlt(
            @Value("${spring.kafka.topics.analytics-profile-event-topic.dlt.name}") String name,
            @Value("${spring.kafka.topics.analytics-profile-event-topic.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.analytics-profile-event-topic.dlt.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    /* ---- redis-retry-error ---- */

    @Bean
    public NewTopic redisRetryErrorTopic(
            @Value("${spring.kafka.topics.redis-retry-error-topic.name}") String name,
            @Value("${spring.kafka.topics.redis-retry-error-topic.partitions}") int partitions,
            @Value("${spring.kafka.topics.redis-retry-error-topic.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic redisRetryErrorTopicDlt(
            @Value("${spring.kafka.topics.redis-retry-error-topic.dlt.name}") String name,
            @Value("${spring.kafka.topics.redis-retry-error-topic.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.redis-retry-error-topic.dlt.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    /* ---- follower-events ---- */

    @Bean
    public NewTopic followerEventTopic(
            @Value("${spring.kafka.topics.follower-events.name}") String name,
            @Value("${spring.kafka.topics.follower-events.partitions}") int partitions,
            @Value("${spring.kafka.topics.follower-events.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic followerEventDlt(
            @Value("${spring.kafka.topics.follower-events.dlt.name}") String name,
            @Value("${spring.kafka.topics.follower-events.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.follower-events.dlt.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    /* ---- profile-view-event ---- */

    @Bean
    public NewTopic profileViewTopic(
            @Value("${spring.kafka.topics.profile-view-event-topic.name}") String name,
            @Value("${spring.kafka.topics.profile-view-event-topic.partitions}") int partitions,
            @Value("${spring.kafka.topics.profile-view-event-topic.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic profileViewTopicDlt(
            @Value("${spring.kafka.topics.profile-view-event-topic.dlt.name}") String name,
            @Value("${spring.kafka.topics.profile-view-event-topic.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.profile-view-event-topic.dlt.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    /* ---- recommendation-request ---- */

    @Bean
    public NewTopic recommendationEventsTopic(
            @Value("${spring.kafka.topics.recommendation.name}") String name,
            @Value("${spring.kafka.topics.recommendation.partitions}") int partitions,
            @Value("${spring.kafka.topics.recommendation.replication-factor}") short replicas
    ) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic recommendationEventsTopicDlt(
            @Value("${spring.kafka.topics.recommendation.dlt.name}") String name,
            @Value("${spring.kafka.topics.recommendation.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.recommendation.dlt.replication-factor}") short replicas
    ) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic requestRecommendationTopic(
            @Value("${spring.kafka.topics.recommendation-request.name}") String name,
            @Value("${spring.kafka.topics.recommendation-request.partitions}") int partitions,
            @Value("${spring.kafka.topics.recommendation-request.replication-factor}") short replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}