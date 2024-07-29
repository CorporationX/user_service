package school.faang.user_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "dice-bear")
@Data
@Component
public class DiceBearConfig {

    private String url;
    private String type;
    private List<String> styles;
}
