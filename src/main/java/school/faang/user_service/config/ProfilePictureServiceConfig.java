package school.faang.user_service.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Data
@Configuration
public class ProfilePictureServiceConfig {

    @Value("${profile-picture-service.extensions.original}")
    private String originalExtension;

    @Value("${profile-picture-service.extensions.resized}")
    private String resizedExtension;

    @Value("${profile-picture-service.dicebear.baseUrl}")
    private String dicebearBaseUrl;

    @Value("${profile-picture-service.styles}")
    private Set<String> styles;

    @Value("${profile-picture-service.resized-dimensions.width}")
    private int width;

    @Value("${profile-picture-service.resized-dimensions.height}")
    private int height;

    private String diceBearBaseUrlWithStyle;

    @PostConstruct
    public void initialize() {
        diceBearBaseUrlWithStyle = dicebearBaseUrl + styles.iterator().next() + "/svg?seed=";
    }
}
