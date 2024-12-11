package school.faang.user_service.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.event.kafka.PostPublishedKafkaEvent;

@Service
public class FeedHeatKafkaProducer extends AbstractKafkaProducer<PostPublishedKafkaEvent> {

    @Value("${kafka.topics.feed-heat}")
    private String feedHeatKafkaTopic;

    public FeedHeatKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return feedHeatKafkaTopic;
    }
}

