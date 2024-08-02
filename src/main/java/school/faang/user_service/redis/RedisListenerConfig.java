package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
@Configuration
public class RedisListenerConfig {
    private final JedisConnectionFactory connectionFactory;
    private final RedisMessageSubscriber redisMessageSubscriber;

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisMessageSubscriber, new ChannelTopic("user_ban"));
        return container;
    }
}