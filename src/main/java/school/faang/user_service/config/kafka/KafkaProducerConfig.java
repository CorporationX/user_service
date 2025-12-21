package school.faang.user_service.config.kafka;


import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.producer.acks}")
    private String akcs;
    @Value(value = "${spring.kafka.producer.retries}")
    private int retries;
    @Value(value = "${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    private int connections;
    @Value(value = "${spring.kafka.producer.properties.enable.idempotence}")
    private boolean idempotence;

    @Bean("producerFactory")
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.ACKS_CONFIG, akcs);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);

        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>();
        jsonSerializer.setAddTypeInfo(true);

        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, connections);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), jsonSerializer);
    }

    @Bean("kafkaTemplate")
    public KafkaTemplate<String, Object> kafkaTemplate(
            @Qualifier("producerFactory") ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}