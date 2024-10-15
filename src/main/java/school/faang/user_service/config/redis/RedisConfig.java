package school.faang.user_service.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.message.MentorshipRequestMessage;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.channels.mentorship-request-topic}")
    private String topic;

    @Bean
    ChannelTopic mentorshipRequestTopic() {
        return new ChannelTopic(topic);
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, MentorshipRequestDto> redisTemplate() {
        final RedisTemplate<String, MentorshipRequestDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        Jackson2JsonRedisSerializer<MentorshipRequestMessage> serializer =
                new Jackson2JsonRedisSerializer<>(MentorshipRequestMessage.class);

        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
}
