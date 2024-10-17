package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.event.FollowerEventDto;
import school.faang.user_service.dto.event.GoalCompletedEventDto;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final ObjectMapper objectMapper;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisTemplate<String, FollowerEventDto> followerEventredisTemplate() {
        RedisTemplate<String, FollowerEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, FollowerEventDto.class));
        return template;
    }

    RedisTemplate<String, GoalCompletedEventDto> redisGoalTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objMapper) {
        RedisTemplate<String, GoalCompletedEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, FollowerEventDto.class));
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objMapper, GoalCompletedEventDto.class));
        return template;
    }


    @Bean(value = "followerEventChannel")
    ChannelTopic followerEventChannelTopic(
            @Value("${spring.data.redis.channels.follower-channel.name}") String name) {
        return new ChannelTopic(name);
    }

    @Bean(value = "goalCompletedTopic")
    public ChannelTopic goalCompletedTopic(
            @Value("${spring.data.redis.channels.goal-event-channel.name}") String topic) {
        return new ChannelTopic(topic);
    }
}
