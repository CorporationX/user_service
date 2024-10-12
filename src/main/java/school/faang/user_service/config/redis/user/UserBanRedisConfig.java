package school.faang.user_service.config.redis.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.service.user.UserBanListener;

@Configuration
public class UserBanRedisConfig {
    @Value("${redis.banner.topic}")
    private String userBanTopic;

    @Bean
    public MessageListenerAdapter listenerAdapter(UserBanListener listener) {
        return new MessageListenerAdapter(listener);
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, userBanTopic());
        return container;
    }

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(userBanTopic);
    }
}
