package school.faang.user_service.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties("spring.kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private int poolSize;
    private Map<String, Topic> topics = new HashMap<>();
}
