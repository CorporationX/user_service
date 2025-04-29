package school.faang.user_service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI api() {
        return  new OpenAPI()
                .servers(
                        List.of(
                                new Server().url("http://localhost:8080").description("Local server")
                        )
                )
                .info(
                        new Info()
                                .title("Our user API")
                                .version("v1")
                                .description("API для управления пользователями")
                                .contact(new io.swagger.v3.oas.models.info.Contact()
                                        .name("Support Team")
                                        .url("http://localhost:8080/support")
                                        .email("support@example.com"))
                                .license(new io.swagger.v3.oas.models.info.License()
                                        .name("Apache 2.0")
                                        .url("http://www.apache.org/licenses/LICENSE-2.0"))
                );
    }
}
