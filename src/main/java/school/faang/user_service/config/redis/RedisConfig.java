package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.event.FollowerEventDto;

@Setter
@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisConfig {
    private int port;
    private String host;
    private final RedisChannelNames redisChannelNames;
    private final ObjectMapper objectMapper;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    RedisTemplate<String, FollowerEventDto> followerEventredisTemplate() {
        RedisTemplate<String, FollowerEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, FollowerEventDto.class));
        return template;
    }

    @Bean(value = "followerEventChannel")
    ChannelTopic followerEventChannelTopic() {
        return new ChannelTopic(redisChannelNames.getFollowerEventChannel());
    }
}
