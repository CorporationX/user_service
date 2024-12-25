package school.faang.user_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    public static final String KAFKA_PRODUCER_NAME = "kafkaProducer";

    private final KafkaTopicsProps kafkaTopicsProps;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configs.put("spring.json.add.type.headers", false);
        configs.put("spring.json.trusted.packages", "*");
        configs.put("spring.json.encoding", "UTF-8");
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        return AdminClient.create(configs);
    }

    @Bean
    public NewTopic indexUserTopic() {
        KafkaTopicsProps.Topic userIndexingTopic = kafkaTopicsProps.getUserIndexingTopic();
        return TopicBuilder.name(userIndexingTopic.getName())
                .partitions(userIndexingTopic.getPartitions())
                .replicas(userIndexingTopic.getReplicationFactor())
                .build();
    }

    @Bean
    public NewTopic indexEventTopic() {
        KafkaTopicsProps.Topic eventIndexingTopic = kafkaTopicsProps.getEventIndexingTopic();
        return TopicBuilder.name(eventIndexingTopic.getName())
                .partitions(eventIndexingTopic.getPartitions())
                .replicas(eventIndexingTopic.getReplicationFactor())
                .build();
    }

    @Bean
    public NewTopic updateUserTopic() {
        KafkaTopicsProps.Topic updateUserTopic = kafkaTopicsProps.getUpdateUserTopic();
        return TopicBuilder.name(updateUserTopic.getName())
                .partitions(updateUserTopic.getPartitions())
                .replicas(updateUserTopic.getReplicationFactor())
                .build();
    }
}
