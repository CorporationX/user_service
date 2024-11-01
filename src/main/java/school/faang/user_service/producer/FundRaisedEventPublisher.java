package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.FundRaisedEvent;

@Component
@RequiredArgsConstructor
public class FundRaisedEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.channels.fund-raised-channel.name}")
    private String fundRaisedTopic;

    public void publish(FundRaisedEvent fundRaisedEvent) {
        kafkaTemplate.send(fundRaisedTopic, fundRaisedEvent);
    }
}
