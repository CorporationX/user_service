package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;

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
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory,
                                                       StringRedisSerializer stringRedisSerializer) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        return buildRedisTemplate(connectionFactory, stringRedisSerializer, serializer);
    }

    @Bean
    public RedisTemplate<String, PremiumBoughtEventDto> premiumBoughtEventDtoRedisTemplate(
            JedisConnectionFactory connectionFactory, StringRedisSerializer stringRedisSerializer) {
        Jackson2JsonRedisSerializer<PremiumBoughtEventDto> serializer =
                new Jackson2JsonRedisSerializer<>(PremiumBoughtEventDto.class);
        return buildRedisTemplate(connectionFactory, stringRedisSerializer, serializer);
    }

    private <T> RedisTemplate<String, T> buildRedisTemplate(JedisConnectionFactory connectionFactory,
                                                            StringRedisSerializer stringRedisSerializer,
                                                            Jackson2JsonRedisSerializer<T> serializer) {
        RedisTemplate<String, T> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        return template;
    }
}
