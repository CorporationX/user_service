package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.event.PostFeedDistributionEvent;

@Component
@RequiredArgsConstructor
public class PostFeedDistributionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.post-feed-distribution}")
    private String postFeedDistributionTopic;

    public void send(PostFeedDistributionEvent event) {
        kafkaTemplate.send(postFeedDistributionTopic, event);
    }
}
