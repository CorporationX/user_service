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

    @Value("${spring.data.redis.channel.follower.name}")
    private String followerChannel;

    @Value("${spring.data.redis.channel.unfollow.name}")
    private String unfollowChannel;

    @Value("${spring.data.redis.channel.follower-project.name}")
    private String projectFollowerChannel;

    @Value("${spring.data.redis.channel.unfollow-project.name}")
    private String projectUnfollowChannel;

    @Value("${spring.data.redis.channel.mentorship-channel.name}")
    private String mentorshipChannel;

    @Value("${spring.data.redis.channel.follower-event-channel.name}")
    private String followerEventChannel;

    @Value("${spring.data.redis.channel.search-appearance.name}")
    private String searchAppearanceChannel;
}
