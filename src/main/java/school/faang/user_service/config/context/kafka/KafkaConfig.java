package school.faang.user_service.config.context.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import school.faang.user_service.dto.event.ProfileViewEventDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    public <T> ProducerFactory<String, T> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                kafkaProperties.getProducerProperties(),
                new StringSerializer(),
                new JsonSerializer<>(objectMapper)
        );
    }

    public <T> KafkaTemplate<String, T> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, ProfileViewEventDto> profileViewKafkaTemplate() {
        return kafkaTemplate();
    }

    @Bean
    public KafkaTemplate<String, MentorshipRequestDto> mentorshipKafkaTemplate() {
        return kafkaTemplate();
    }
}
