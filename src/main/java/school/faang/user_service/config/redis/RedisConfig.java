package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.messaging.dto.ProfileVisitEvent;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(redisConfig);
    }

    /**
     * Создаёт и настраивает бин {@link StringRedisTemplate} для работы с Redis.
     *
     * @param factory фабрика подключения к Redis
     * @return настроенный {@link StringRedisTemplate}
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private <T> RedisTemplate<String, T> buildTemplate(
            RedisConnectionFactory factory,
            ObjectMapper mapper,
            Class<T> clazz
    ) {
        var template = new RedisTemplate<String, T>();
        template.setConnectionFactory(factory);

        var serializer = new Jackson2JsonRedisSerializer<>(mapper, clazz);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, SearchAppearanceEvent> searchAppearanceEventRedisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper redisObjectMapper
    ) {
        return buildTemplate(factory, redisObjectMapper, SearchAppearanceEvent.class);
    }

    @Bean
    public RedisTemplate<String, ProfileVisitEvent> profileVisitEventRedisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper redisObjectMapper
    ) {
        return buildTemplate(factory, redisObjectMapper, ProfileVisitEvent.class);
    }
}
