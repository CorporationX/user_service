package school.faang.user_service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .version("1.0.0")
                        .description("A user-service API, managing user actions in CorporationX project.")
                        .contact(new Contact()
                                .name("CorporationX support team")
                                .email("it.support@corporationx.com"))
                        .license(new License()
                                .name("Proprietary License")
                                .url("https://www.corporation-x.com/user-service/license"))
                );
    }
}
