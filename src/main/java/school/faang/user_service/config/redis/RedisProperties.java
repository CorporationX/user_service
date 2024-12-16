package school.faang.user_service.config.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class RedisProperties {

    @Value("${spring.data.redis.channel.event-participation.name}")
    private String topicEventParticipation;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.channel.follower}")
    private String followerChannel;

    @Value("${spring.data.redis.channel.unfollow}")
    private String unfollowChannel;

    @Value("${spring.data.redis.channel.follower-project.name}")
    private String followerProjectChannel;

    @Value("${spring.data.redis.channel.unfollow-project.name}")
    private String unfollowProjectChannel;
}
