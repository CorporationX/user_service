package school.faang.user_service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "services.dicebear")
@Component
@Getter
@Setter
public class DiceBearProperties {
    String url;
    String version;
    List<String> styles;
    String fileType;
    String urlPattern;
}
