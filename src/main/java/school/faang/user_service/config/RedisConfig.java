package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.listener.UserBanListener;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(UserBanListener userBanListener) {
        return new MessageListenerAdapter(userBanListener);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connection,
                                                        MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connection);

        String topicName = redisProperties.getChannels().get("user-service");
        container.addMessageListener(listenerAdapter, new PatternTopic(topicName));
        return container;
    }
}
