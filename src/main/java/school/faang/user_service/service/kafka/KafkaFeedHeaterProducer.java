package school.faang.user_service.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaFeedHeaterProducer extends KafkaAbstractProducer {

    @Value("${spring.kafka.producer.news-feed-topic}")
    private String heaterTopicName;

    public KafkaFeedHeaterProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    @Async("kafkaThreadPool")
    public void sendMessageAsync(Object message) {
        var future = kafkaTemplate.send(heaterTopicName, message);
        handleFuture(future);
    }
}


