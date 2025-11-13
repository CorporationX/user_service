package school.faang.user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        SecurityScheme headerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .name("x-user-id")
                .in(SecurityScheme.In.HEADER);

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("userIdAuth", headerAuth))
                .addSecurityItem(new SecurityRequirement()
                        .addList("userIdAuth"));
    }
}