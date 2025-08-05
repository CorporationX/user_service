package school.faang.user_service.config.kafka;

import org.springframework.context.annotation.Configuration;

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
}
