package school.faang.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.publisher.ProjectFollowerEventPublisher;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.channels.follower_view.name}")
    private String followerViewChannelName;
    @Value("${spring.data.redis.channels.event-start.name}")
    private String eventStartTopicName;

    @Value("${spring.data.redis.channels.project_follower_channel.name}")
    private String projectFollowerTopicName;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info("Redis host:{}, port:{}", host, port);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    ChannelTopic followerTopic() {
        return new ChannelTopic(followerViewChannelName);
    }


    @Bean
    public ChannelTopic eventStartTopic() {
        return new ChannelTopic(eventStartTopicName);
    }
    @Bean
    ChannelTopic projectFollowerTopic(){
        return new ChannelTopic(projectFollowerTopicName);
    }

}
