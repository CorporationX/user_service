package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.event.MentorshipAcceptedEventDto;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final ObjectMapper objectMapper;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisTemplate<String, MentorshipAcceptedEventDto> mentorshipAcceptedEventredisTemplate() {
        RedisTemplate<String, MentorshipAcceptedEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, MentorshipAcceptedEventDto.class));
        return template;
    }

    @Bean(value = "mentorshipEventChannel")
    ChannelTopic mentorshipEventChannelTopic(
            @Value("${spring.data.redis.channels.mentorship-channel.name}") String name) {
        return new ChannelTopic(name);
    }
}
