package school.faang.user_service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "avatar")
@Component
@Getter
@Setter
public class AvatarProperties {
    private String smallPattern;
    private int smallSize;
    private String normalPattern;
    private int normalSize;
}
