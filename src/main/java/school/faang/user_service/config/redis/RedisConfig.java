package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;
import school.faang.user_service.dto.user.ProfileViewEventDto;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory,
                                                       ObjectMapper javaTimeModuleObjectMapper) {
        return buildRedisTemplate(connectionFactory, Object.class, javaTimeModuleObjectMapper);
    }

    @Bean
    public RedisTemplate<String, ProfileViewEventDto> profileViewEventDtoRedisTemplate(
            JedisConnectionFactory connectionFactory, ObjectMapper javaTimeModuleObjectMapper) {
        return buildRedisTemplate(connectionFactory, ProfileViewEventDto.class, javaTimeModuleObjectMapper);
    }

    @Bean
    public RedisTemplate<String, PremiumBoughtEventDto> premiumBoughtEventDtoRedisTemplate(
            JedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, PremiumBoughtEventDto.class, objectMapper);
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
}