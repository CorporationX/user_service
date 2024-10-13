package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisConfig {

    @Value("redis.host")
    private String host;

    @Value("redis.port")
    private int port;

    @Value("redis.followingChannel")
    private String followingChannel;

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        RedisConnectionFactory factory = new JedisConnectionFactory(config);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        return template;
    }

    @Bean(name = "following_channel")
    public ChannelTopic followingChannel() {
        return new ChannelTopic(followingChannel);
    }
}
