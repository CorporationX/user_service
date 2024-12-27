package school.faang.user_service.config.redis;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(String host, int port, Channel channel) {

    @Builder
    public record Channel(String mentorshipChannel,
                          String subscriptionChannel,
                          String recommendationChannel,
                          String userBanChannel,
                          String mentorshipRequest,
                          String followerChannel,
                          String eventStartEventChannel) {
    }
}
