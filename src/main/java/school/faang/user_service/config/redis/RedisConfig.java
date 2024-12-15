package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.channel.user-ban-channel:user_ban}")
    private String userBanTopic;

    private final RedisProperties redisProperties;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        log.info("Jedis client for Redis configured: host = {}, port = {}", redisProperties.getHost(), redisProperties.getPort());
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainerConfig(
            RedisConnectionFactory connectionFactory,
            UserBanSubscriber userBanSubscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        MessageListenerAdapter listenerAdapter = createListenerAdapter(userBanSubscriber);

        container.addMessageListener(listenerAdapter, new PatternTopic(userBanTopic));

        log.info("RedisMessageListenerContainer configured with topic: {}", userBanTopic);

        return container;
    }

    private MessageListenerAdapter createListenerAdapter(UserBanSubscriber userBanSubscriber) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(userBanSubscriber);
        listenerAdapter.setDefaultListenerMethod("onMessage");
        return listenerAdapter;
    }
}
