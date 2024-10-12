package school.faang.user_service.config.async;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "threads")
@Data
public class PropertiesOfAsyncThreads {

    private int threadPoolSize;
    private int queueCapacity;

}
