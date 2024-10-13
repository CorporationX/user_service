package school.faang.user_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestRedisConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return mock(RedisConnectionFactory.class);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return mock(RedisTemplate.class);
    }


    @Bean
    public RedisMessageListenerContainer redisContainer() {
        return mock(RedisMessageListenerContainer.class);
    }

    @Bean
    public MessageListenerAdapter listenerAdapter() {
        return mock(MessageListenerAdapter.class);
    }

    @Bean
    public RedisKeyValueAdapter redisKeyValueAdapter() {
        return mock(RedisKeyValueAdapter.class);
    }
}
