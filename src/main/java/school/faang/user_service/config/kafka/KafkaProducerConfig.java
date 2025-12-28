package school.faang.user_service.config.kafka;


import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import school.faang.user_service.dto.post.PostToFeedEvent;
import school.faang.user_service.dto.recommendation.RecommendationEventDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.producer.acks}")
    private String acks;
    @Value(value = "${spring.kafka.producer.retries}")
    private int retries;
    @Value(value = "${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    private int connection;
    @Value(value = "${spring.kafka.producer.properties.enable.idempotence}")
    private boolean idempotence;

    @Bean
    public ProducerFactory<String, PostToFeedEvent> postToFeedProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, connection);

        JsonSerializer<PostToFeedEvent> serializer = new JsonSerializer<>();
        serializer.setAddTypeInfo(true);

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), serializer);
    }

    @Bean
    public ProducerFactory<String, RecommendationEventDto> recommendationProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);

        JsonSerializer<RecommendationEventDto> serializer = new JsonSerializer<>();
        serializer.setAddTypeInfo(true);

        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, connection);

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, PostToFeedEvent> postToFeedKafkaTemplate() {
        return new KafkaTemplate<>(postToFeedProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, RecommendationEventDto> recommendationKafkaTemplate() {
        return new KafkaTemplate<>(recommendationProducerFactory());
    }
}