package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.listener.RedisBanMessageListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisBanMessageListener banMessageListener;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.ban-user-channel.name}")
    private String banUserTopic;

    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    private String followerEvent;

    @Value("${spring.data.redis.channels.event-start-channel.name}")
    private String eventStartTopic;

    @Value("${spring.data.redis.channels.recommendation-received-channel.name}")
    private String recommendationReceived;

    @Value("${spring.data.redis.channels.follow-project-channel.name}")
    private String followProjectTopic;

    @Value("${spring.data.redis.channels.goal-completed-event-channel.name}")
    private String goalCompletedTopic;

    @Value("${spring.data.redis.channels.skill-acquired-channel.name}")
    private String skillAcquired;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(banUserMessageListenerAdapter(), banUserChannelTopic());
        return container;
    }

    @Bean
    public MessageListenerAdapter banUserMessageListenerAdapter() {
        return new MessageListenerAdapter(banMessageListener);
    }

    @Bean
    public ChannelTopic banUserChannelTopic() {
        return new ChannelTopic(banUserTopic);
    }

    @Bean
    public ChannelTopic followerTopic() {
        return new ChannelTopic(followerEvent);
    }

    @Bean
    public ChannelTopic eventStartTopic() {
        return new ChannelTopic(eventStartTopic);
    }

    @Bean
    public ChannelTopic recommendationReceivedTopic() {
        return new ChannelTopic(recommendationReceived);
    }

    @Bean
    public ChannelTopic followProjectTopic() {
        return new ChannelTopic(followProjectTopic);
    }

    @Bean
    public ChannelTopic goalCompletedTopic() {
        return new ChannelTopic(goalCompletedTopic);
    }

    @Bean
    public ChannelTopic skillAcquiredTopic() {
        return new ChannelTopic(skillAcquired);
    }
}
