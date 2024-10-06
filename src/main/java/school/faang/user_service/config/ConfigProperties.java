package school.faang.user_service.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "threads")
@Data
public class ConfigProperties {

    private int threadPoolSize;
    private int queueCapacity;

}
