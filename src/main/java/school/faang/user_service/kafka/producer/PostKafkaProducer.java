package school.faang.user_service.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.event.kafka.PostPublishedKafkaEvent;

@Service
public class PostKafkaProducer extends AbstractKafkaProducer<PostPublishedKafkaEvent> {

    @Value("${kafka.topics.post}")
    private String postKafkaTopic;

    public PostKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return postKafkaTopic;
    }
}

