package school.faang.user_service.kafka;

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
}
