package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.event.RedisUserBanListener;

@Configuration
public class RedisSubscribeConfig {

    @Value("${spring.data.redis.settings.user_ban.topic}")
    private String userBanTopic;
    @Value("${spring.data.redis.settings.user_ban.listener}")
    private String userBanListener;

    @Bean
    public MessageListenerAdapter messageListener(RedisUserBanListener listener) {
        return new MessageListenerAdapter(listener, userBanListener);
    }

    @Bean
    public RedisMessageListenerContainer container(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(userBanTopic));
        return container;
    }
}
