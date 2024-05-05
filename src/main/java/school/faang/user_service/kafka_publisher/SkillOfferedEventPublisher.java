package school.faang.user_service.kafka_publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.SkillOfferedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillOfferedEventPublisher implements MessagePublisher<SkillOfferedEvent> {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.data.kafka.channels.skill-channel.name}")
    private String skillEventChannel;

    @Override
    public void publish(SkillOfferedEvent skillOfferedEvent){
        try {
            kafkaTemplate.send(skillEventChannel, objectMapper.writeValueAsString(skillOfferedEvent));
            log.info("SkillOffered event publish: " + skillOfferedEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
