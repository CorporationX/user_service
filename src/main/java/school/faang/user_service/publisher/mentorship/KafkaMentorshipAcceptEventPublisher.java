package school.faang.user_service.publisher.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMentorshipAcceptEventPublisher implements MentorshipAcceptEventPublisher {

    private final KafkaTemplate<String, MentorshipRequestDto> kafkaTemplate;

    @Value("${spring.kafka.topics.mentorship-accept}")
    private String topic;

    @Override
    public void publish(MentorshipRequestDto event) {
        kafkaTemplate.send(topic, event.getReceiverId().toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to send MentorshipRequestAcceptedEvent: {}", event, exception);
                    }
                });
    }
}
