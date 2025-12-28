package school.faang.user_service.messages.kafka.producers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.post.PostToFeedEvent;

@Component
public class PostToFeedProducer extends AbstractPublishKafka<PostToFeedEvent> {
    public PostToFeedProducer(
            @Qualifier("postToFeedKafkaTemplate")
            KafkaTemplate<String, PostToFeedEvent> kafkaTemplate,
            @Value("${spring.kafka.topics.post-to-feed}")
            String postEvent
    ) {
        super(kafkaTemplate, postEvent);
    }
}