package school.faang.user_service.config.transactions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@Configuration
public class KafkaTransactionManagerConfig {

    @Bean
    public KafkaTransactionManager<String, String> kafkaTransactionManager(
            ProducerFactory<String, String> producerFactory) {

        KafkaTransactionManager<String, String> manager = new KafkaTransactionManager<>(producerFactory);
        manager.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        return manager;
    }
}