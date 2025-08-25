package school.faang.user_service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${spring.kafka.topics.user-create.name}")
    private String userCreateTopic;
    @Value("${spring.kafka.topics.user-update.name}")
    private String userUpdateTopic;

    @Bean
    public NewTopic userCreate() {
        return TopicBuilder.name(userCreateTopic).build();
    }

    @Bean
    public NewTopic userUpdate() {
        return TopicBuilder.name(userUpdateTopic).build();
    }
}
