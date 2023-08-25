package school.faang.user_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;



@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User service")
                        .description("service for users")
                        .version("1.0.0")
                        .license(new License()
                                .name("Your License Name")
                                .url("Your License URL")
                        )
                );
    }
}
