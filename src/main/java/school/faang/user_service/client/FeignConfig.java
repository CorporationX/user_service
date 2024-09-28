package school.faang.user_service.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.config.api.ApiProperties;
import school.faang.user_service.config.context.UserContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext,
                                                     List<ApiProperties> apiProperties) {
        Map<String, List<String>> endpointRequiredHeaders = new HashMap<>();
        apiProperties.forEach(p -> endpointRequiredHeaders.put(p.getEndpoint(), p.getRequiredHeaders()));

        return new FeignUserInterceptor(userContext, endpointRequiredHeaders);
    }
}
