package school.faang.user_service.config.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
public abstract class ApiProperties {
    private String endpoint;
    private List<String> headers;
}
