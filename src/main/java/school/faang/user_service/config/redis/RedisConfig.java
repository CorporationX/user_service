package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.listener.UserBanListener;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public MessageListenerAdapter listenerAdapter(UserBanListener userBanListener) {
        return new MessageListenerAdapter(userBanListener);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connection, MessageListenerAdapter adapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connection);

        String topicName = redisProperties.getChannels().get("user-service");
        container.addMessageListener(adapter, new PatternTopic(topicName));
        return container;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }
}
