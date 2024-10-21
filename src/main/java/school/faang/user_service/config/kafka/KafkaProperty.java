package school.faang.user_service.config.kafka;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KafkaProperty {
    private final String bootstrapServers = "localhost:9092";
    private final String acksConfig = "1";
    private final Integer retries = 3;
    private final Integer lingerMsConfig = 5;
    private final Integer batchSizeConfig = 16384;
    private final String topicName = "Goal_Channel";
}
