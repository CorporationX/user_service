package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;

@Configuration
public class redisConfig {

    @Value("${data.redis.channels.mentorship-request-topic}")
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
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }
}
