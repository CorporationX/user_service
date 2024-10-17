package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.event.GoalCompletedEventDto;

@Configuration
public class RedisConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisTemplate<String, GoalCompletedEventDto> redisGoalTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objMapper) {
        RedisTemplate<String, GoalCompletedEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objMapper, GoalCompletedEventDto.class));
        return template;
    }

    @Bean(value = "goalCompletedTopic")
    public ChannelTopic goalCompletedTopic(
            @Value("${spring.data.redis.channels.goal-event-channel.name}") String topic) {
        return new ChannelTopic(topic);
    }
}