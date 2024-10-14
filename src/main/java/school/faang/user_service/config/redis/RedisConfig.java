package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.service.user.UserBanListener;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.user.ProfileViewEventDto;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${redis.banner.topic}")
    private String userBanTopic;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setDefaultSerializer(new StringRedisSerializer());
        return template;
    }

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

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory,
                                                       ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, Object.class, objectMapper);
    }

    @Bean
    public RedisTemplate<String, ProfileViewEventDto> profileViewEventDtoRedisTemplate(
            JedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, ProfileViewEventDto.class, objectMapper);
    }

    private <T> RedisTemplate<String, T> buildRedisTemplate(JedisConnectionFactory jedisConnectionFactory,
                                                            Class<T> clazz, ObjectMapper objectMapper) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, clazz);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        return template;
}
