package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.publis.listener.UserBanListener;
import school.faang.user_service.redis.listener.AuthorBanListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;
    private final UserBanListener userBanListener;
    private final AuthorBanListener authorBanListener;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer redisContainer = new RedisMessageListenerContainer();
        redisContainer.setConnectionFactory(redisConnectionFactory());

        redisContainer.addMessageListener(userBanListenerAdapter(), userBanTopic());
        redisContainer.addMessageListener(authorBanListenerAdapter(), userBanTopic());

        return redisContainer;
    }

    @Bean
    public MessageListener userBanListenerAdapter() {
        return new MessageListenerAdapter(userBanListener);
    }

    @Bean
    public MessageListener authorBanListenerAdapter() {
        return new MessageListenerAdapter(authorBanListener);
    }

    @Bean
    public Topic userBanTopic() {
        return new ChannelTopic(redisProperties.getUserBanChannelName());
    }
}
