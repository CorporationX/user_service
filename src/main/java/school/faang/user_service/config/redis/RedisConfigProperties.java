package school.faang.user_service.config.redis;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Data
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisConfigProperties {

    @Min(1025)
    @Max(65536)
    private int port;

    @NotBlank
    private String host;

    private Channel channel;

    @Data
    public static class Channel {
        @NotBlank
        private String mentorshipRequested;

        @NotBlank
        private String goalCompleted;
    }
}

