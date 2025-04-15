package school.faang.user_service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "profile-pic")
public class ProfilePicProperties {
    private long maxSize;
    private int largePhotoSize;
    private int smallPhotoSize;
    private String folderName;
}
