package school.faang.user_service.messaging.config.redispubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import school.faang.user_service.messaging.consumer.redispubsub.ProfileVisitEventConsumer;
import school.faang.user_service.messaging.consumer.redispubsub.SearchAppearanceEventConsumer;

/**
 * Конфигурация Pub/Sub механизма для Redis.
 * <p>
 * Определяет каналы для событий аналитики и настраивает контейнер слушателей,
 * которые будут обрабатывать сообщения из Redis:
 * <ul>
 *   <li>{@link SearchAppearanceEventConsumer} — обработка событий появления профиля в поиске</li>
 *   <li>{@link ProfileVisitEventConsumer} — обработка событий посещения профиля</li>
 * </ul>
 * <p>
 * Каналы подтягиваются из application.yml (см. префикс {@code kafka.topics.*}),
 * и регистрируются как {@link ChannelTopic}.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@Configuration
@RequiredArgsConstructor
public class RedisPubSubConfig {
    private final SearchAppearanceEventConsumer searchAppearanceEventConsumer;
    private final ProfileVisitEventConsumer profileVisitEventConsumer;
    @Value("${kafka.topics.search-appearance}")
    private String searchAppearanceTopic;
    @Value("${kafka.topics.profile-visit}")
    private String profileVisitTopic;

    @Bean
    public RedisMessageListenerContainer redisSearchAppearanceEventContainer(RedisConnectionFactory connectionFactory) {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(searchAppearanceEventConsumer, searchAppearanceTopic());
        container.addMessageListener(profileVisitEventConsumer, profileVisitTopic());
        return container;
    }

    @Bean
    public ChannelTopic searchAppearanceTopic() {
        return new ChannelTopic(searchAppearanceTopic);
    }

    @Bean
    public ChannelTopic profileVisitTopic() {
        return new ChannelTopic(profileVisitTopic);
    }
}
