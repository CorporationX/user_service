package school.faang.user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme headerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("x-user-id");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("userIdAuth");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("userIdAuth", headerAuth))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("User Service API")
                        .description("API для сервиса пользователей")
                        .version("1.0")
                );
    }
}