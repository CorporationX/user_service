package school.faang.user_service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import school.faang.user_service.kafka.KafkaTopic;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userCreated() {
        return TopicBuilder.name(KafkaTopic.USER_CREATED.getName()).build();
    }
}
