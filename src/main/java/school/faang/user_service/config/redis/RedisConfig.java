package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.publis.listener.UserBanListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public MessageListenerAdapter userBanAdapter(UserBanListener userBanListener) {
        return new MessageListenerAdapter(userBanListener);
    }

    @Bean
    public Topic userBanTopic() {
        return new ChannelTopic(redisProperties.getUserBanChannelName());
    }

    @Bean
    public RedisMessageListenerContainer userBanListenerContainer(RedisConnectionFactory connectionFactory, MessageListenerAdapter userBanAdapter) {
        RedisMessageListenerContainer redisContainer = new RedisMessageListenerContainer();
        redisContainer.setConnectionFactory(connectionFactory);

        redisContainer.addMessageListener(userBanAdapter, userBanTopic());

        return redisContainer;
    }
}
