package school.faang.user_service.kafka.producer;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import school.faang.user_service.kafka.KafkaTopics;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicsConfig {
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setAutoCreate(true);
        return admin;
    }

    @Bean
    public NewTopic analyticsCreatedTopic(
            @Value("${spring.kafka.topics.analytics-created.name}") String name,
            @Value("${spring.kafka.topics.analytics-created.partitions}") int partitions,
            @Value("${spring.kafka.topics.analytics-created.replication-factor}") short replicas
    ) {
        return TopicBuilder
                .name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic analyticsCreatedDlt(
            @Value("${spring.kafka.topics.analytics-created.dlt.name}") String name,
            @Value("${spring.kafka.topics.analytics-created.dlt.partitions}") int partitions,
            @Value("${spring.kafka.topics.analytics-created.dlt.replication-factor}") short replicas
    ) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
