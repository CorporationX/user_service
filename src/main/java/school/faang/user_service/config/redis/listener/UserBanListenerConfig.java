package school.faang.user_service.config.redis.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.service.user.UserBanListener;

@Configuration
public class UserBanListenerConfig {
    @Bean
    public MessageListenerAdapter userBanListenerAdapter(UserBanListener listener) {
        return new MessageListenerAdapter(listener);
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter userBanListenerAdapter,
                                                   Topic userBanTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(userBanListenerAdapter, userBanTopic);
        return container;
    }
}
