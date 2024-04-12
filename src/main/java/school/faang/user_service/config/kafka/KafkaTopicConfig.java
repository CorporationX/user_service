package school.faang.user_service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.data.kafka.channels.follower}")
    private String followerTopic;

    @Bean
    public NewTopic followerTopic() {
        return TopicBuilder
                .name(followerTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
