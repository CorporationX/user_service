package school.faang.user_service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
/**
 *     Используется для конфигурирования топиков, пример:
 *     @Bean
 *     public NewTopic accountTransactionCommands() {
 *         return TopicBuilder.name("account.transaction.commands").build();
 *     }
 *     При запуске приложения создаст топик "account.transaction.commands", если его нет
 */
public class KafkaTopicConfig {

    @Bean
    public NewTopic userCreate() {
        return TopicBuilder.name("user.create").build();
    }

    @Bean
    public NewTopic userUpdate() {
        return TopicBuilder.name("user.update").build();
    }
}
