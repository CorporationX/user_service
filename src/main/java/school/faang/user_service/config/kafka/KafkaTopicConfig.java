package school.faang.user_service.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewEventChannel;

    @Value("${spring.data.kafka.port}")
    private String port;

    @Value("${spring.data.kafka.host}")
    private String host;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%s", host, port));
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic profileViewEventTopic() {
        return new NewTopic(profileViewEventChannel, 1, (short) 1);
    }
}
