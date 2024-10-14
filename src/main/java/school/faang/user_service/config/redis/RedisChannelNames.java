package school.faang.user_service.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class RedisChannelNames {

    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    String followerEventChannel;

    @Value("${spring.data.redis.channels.mentorship-channel}")
    private String mentorshipEventChannel;
}
