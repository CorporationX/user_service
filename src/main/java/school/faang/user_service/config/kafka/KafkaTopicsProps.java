package school.faang.user_service.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.topics")
@Data
public class KafkaTopicsProps {

    private Topic userIndexingTopic;
    private Topic eventIndexingTopic;
    private Topic updateUserTopic;

    @Data
    public static class Topic {
        private String name;
        private Integer partitions;
        private Integer replicationFactor;
    }
}
