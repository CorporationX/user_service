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

    @Value("data.redis.host")
    private String host;

    @Value("data.redis.port")
    private int port;

    @Value("data.redis.channels.follower-channel.name")
    private String followerChannel;

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        RedisConnectionFactory factory = new JedisConnectionFactory(config);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        return template;
    }

    @Bean(name = "follower-channel")
    public ChannelTopic followerChannel() {
        return new ChannelTopic(followerChannel);
    }
}
