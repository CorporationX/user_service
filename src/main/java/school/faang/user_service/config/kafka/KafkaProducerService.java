package school.faang.user_service.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate
                .send(topic, message)
                .whenComplete(
                        (result, ex) -> {
                            if (ex == null) {
                                log.info(
                                        "The message was sent to the topic {}: {}", topic, message);
                            } else {
                                log.error("An error occurred while sending message", ex);
                            }
                        });
    }
}
