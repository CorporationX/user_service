package school.faang.user_service.messages.kafka.producers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostToFeedProducer extends AbstractPublishKafka {
    public PostToFeedProducer(
            @Qualifier("kafkaTemplate")
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${spring.kafka.topics.post-to-feed}")

            String postEvent
    ) {
        super(kafkaTemplate, postEvent);
    }
}