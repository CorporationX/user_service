package school.faang.user_service.client;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.config.api.ApiProperties;
import school.faang.user_service.config.context.UserContext;

import java.util.List;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext,
                                                     List<ApiProperties> apiProperties) {
        return new FeignUserInterceptor(userContext, apiProperties);
    }
}
