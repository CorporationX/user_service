package school.faang.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
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

    @Value("${spring.data.redis.channels.project_follower_channel.name}")
    private String projectFollowerTopicName;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        log.info("Redis host:{}, port:{}", host, port);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        return redisTemplate;
    }

    @Bean
    ChannelTopic followerTopic() {
        return new ChannelTopic(followerViewChannelName);
    }

    @Bean
    EventPublisher projectFollowerPublisher() {
        return new ProjectFollowerEventPublisher(redisTemplate(redisConnectionFactory()), projectFollowerTopic());
    }

    @Bean
    ChannelTopic projectFollowerTopic(){
        return new ChannelTopic(projectFollowerTopicName);
    }

}
