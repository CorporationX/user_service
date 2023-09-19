package school.faang.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.messaging.MessagePublisher;
import school.faang.user_service.messaging.ProfileViewEventPublisher;
import school.faang.user_service.messaging.ProjectFollowerEventPublisher;
import school.faang.user_service.messaging.events.ProfileViewEvent;
import school.faang.user_service.messaging.events.ProjectFollowerEvent;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerChannelName;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        System.out.println(port);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    MessagePublisher viewProfilePublisher(){
        return new ProfileViewEventPublisher(redisTemplate(),viewProfileTopic());
    }
    @Bean
    public ChannelTopic viewProfileTopic() {
        return new ChannelTopic("viewProfileTopic");
    }


    @Bean
    MessagePublisher followerPublisher(){
        return new ProjectFollowerEventPublisher(redisTemplate(), folowerTopic());
    }
    @Bean
    public ChannelTopic folowerTopic() {
        return new ChannelTopic(followerChannelName);
    }


}
