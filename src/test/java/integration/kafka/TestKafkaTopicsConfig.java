package integration.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@Configuration
public class TestKafkaTopicsConfig {

    @Bean
    public NewTopic premiumRequestTopic() {
        NewTopic topic = TopicBuilder.name("premium-payment-request-topic")
                .partitions(1)
                .replicas(1)
                .build();
        log.info("Created topic: {}", topic.name());
        return topic;
    }

    @Bean
    public NewTopic premiumResponseTopic() {
        NewTopic topic = TopicBuilder.name("premium-payment-response-topic")
                .partitions(1)
                .replicas(1)
                .build();
        log.info("Created topic: {}", topic.name());
        return topic;
    }
}
