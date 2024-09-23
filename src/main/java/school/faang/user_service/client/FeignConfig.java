package school.faang.user_service.client;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.externalApis.ExternalApisProperties;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext,
                                                     ExternalApisProperties externalApisProperties) {
        return new FeignUserInterceptor(userContext, externalApisProperties);
    }
}
