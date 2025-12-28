package school.faang.user_service.messages.kafka.producers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEventDto;

@Component
public class RecommendationPublisher extends AbstractPublishKafka<RecommendationEventDto> {

    public RecommendationPublisher(
            @Qualifier("recommendationKafkaTemplate") KafkaTemplate<String, RecommendationEventDto> kafkaTemplate,
            @Value("${spring.kafka.topics.recommendation}") String eventRecommendation) {
        super(kafkaTemplate, eventRecommendation);
    }
}