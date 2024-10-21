package school.faang.user_service.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.kafka.KafkaProperty;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.service.KafkaPublisherService;

@Service
@RequiredArgsConstructor
public class KafkaPublisherServiceImpl implements KafkaPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperty kafkaProperty;

    @Override
    public void sendMessage(GoalCompletedEvent message) {
        kafkaTemplate.send(kafkaProperty.getTopicName(), message);
    }
}
