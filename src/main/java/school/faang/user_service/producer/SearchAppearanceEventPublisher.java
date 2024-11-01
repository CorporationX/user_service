package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.SearchAppearanceEvent;

@Component
@RequiredArgsConstructor
public class SearchAppearanceEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic searchAppearanceTopic;

    public void publish(SearchAppearanceEvent event) {
        kafkaTemplate.send(searchAppearanceTopic.name(), event);
    }

}
