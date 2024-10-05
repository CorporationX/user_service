package school.faang.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    // channels name

    @Value("${spring.data.redis.channels.follower_view.name}")
    private String followerViewChannelName;

    @Value("${spring.data.redis.channels.project_follower_channel.name}")
    private String projectFollowerTopicName;

    @Value("${spring.data.redis.channels.recommendation.name}")
    private String recommendationChannelName;

    private final ObjectMapper objectMapper;

    @Bean
    public String recommendationChannel() {
        return recommendationChannelName;
    }

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
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return redisTemplate;
    }

    @Bean
    ChannelTopic followerTopic() {
        return new ChannelTopic(followerViewChannelName);
    }

    @Bean
    ChannelTopic projectFollowerTopic(){
        return new ChannelTopic(projectFollowerTopicName);
    }

}
