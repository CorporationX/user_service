package school.faang.user_service.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private int port;
    private String host;
    private Channel channel;

    @Data
    public static class Channel {
        private String mentorshipChannel;
        private String followerChannel;
        private String recommendationChannel;
        private String skillChannel;
    }
}
