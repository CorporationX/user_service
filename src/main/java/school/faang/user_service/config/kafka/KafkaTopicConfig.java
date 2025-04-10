package school.faang.user_service.config.kafka;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import school.faang.user_service.config.kafka.properties.ProfilePicTopicProperties;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {
    private final ProfilePicTopicProperties profilePicTopicProperties;

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    public NewTopic profilePicTopic() {
        return TopicBuilder.name(profilePicTopicProperties.name())
                .partitions(profilePicTopicProperties.partitionCount())
                .build();
    }
}
