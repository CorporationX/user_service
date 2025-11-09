package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.kafka.RecommendationEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationProducer {
    private final KafkaTemplate<String, Object> objectKafkaTemplate;

    public void sendToKafka(RecommendationEvent recommendationEvent) {
        String key = recommendationEvent.recommendationId().toString()
                + recommendationEvent.authorId().toString()
                + recommendationEvent.receiverId().toString();
        objectKafkaTemplate.send("new-recommendation", key, recommendationEvent);
        log.info("Сообщение о новой рекомендации с recommendationId: {} отправлено в AnalyticService.",
                recommendationEvent.recommendationId());
    }
}
