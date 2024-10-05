package school.faang.user_service.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserAndFoloweeDto;
import school.faang.user_service.event.user.HeatFeedEvent;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeatFeedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.heat-feed-topic.name}")
    private String topicName;

    public void send(List<UserAndFoloweeDto> users) {
        List<HeatFeedEvent> event = users.stream()
                .map(user -> {
                    return new HeatFeedEvent(user.getId(), user.getFolowees());
                })
                .collect(Collectors.toList());

        kafkaTemplate.send(topicName, event);
        log.info("Sent message to topic {}: {}", topicName, event.toString());
    }
}
