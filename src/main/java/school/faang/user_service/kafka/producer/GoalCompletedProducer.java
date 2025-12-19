package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.kafka.GoalCompletedEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalCompletedProducer {
    private final KafkaTemplate<String, Object> objectKafkaTemplate;

    @Value("${spring.data.kafka.topics.goal-completed.name}")
    private String topicName;

    public void sendGoalCompletedEventToKafka(GoalCompletedEvent event) {
        String key = generateKey(event);
        objectKafkaTemplate.send(topicName, key, event);
        log.info("Новый CompletedGoalEvent с ключом: {} отправлен в Kafka.", event);
    }

    private String generateKey(GoalCompletedEvent goalCompletedEvent) {
        return goalCompletedEvent.goalId().toString() + goalCompletedEvent.userId().toString();
    }
}
