package school.faang.service.user.client;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.service.user.config.context.UserContext;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }
}
