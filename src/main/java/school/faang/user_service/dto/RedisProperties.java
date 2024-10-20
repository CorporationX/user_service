package school.faang.user_service.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "data.redis")
@Data
@Component
public class RedisProperties {
    private String host;
    private int port;
    private Channels channels;

    @Data
    public static class Channels {
        private String mentorshipChannel;
        private String followerChannel;
        private String goalCompletedChannel;
    }
}
