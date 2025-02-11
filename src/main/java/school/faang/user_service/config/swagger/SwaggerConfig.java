package school.faang.user_service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI config() {
        return new OpenAPI().info(new Info()
                        .version("3.0.0")
                        .title("User Service Api")
                        .description("This service is responsible for managing users" +
                                ", their skills, goals, subscriptions, events, and mentorships.")
                        .contact(new Contact()
                                .name("Mihail Svistunov")
                                .email("egzermr@gmail.com")))
                .servers(List.of(new Server()
                        .url("/")));
    }
}
