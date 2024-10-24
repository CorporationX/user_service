package school.faang.user_service.config.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RedisProperties {
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBanChannelName;

    @Value("${spring.data.redis.channels.follower_event_channel.name}")
    private String followerEventChannelName;
}
