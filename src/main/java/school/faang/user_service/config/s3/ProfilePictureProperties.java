package school.faang.user_service.config.s3;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "profile-picture")
public class ProfilePictureProperties {
    private Picture normal;
    private Picture small;

    @Data
    @AllArgsConstructor
    public static class Picture {
        private String path;
        private int size;
    }
}