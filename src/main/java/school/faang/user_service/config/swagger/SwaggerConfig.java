package school.faang.user_service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .description("""
                                REST API для управления пользователями в системе FAANG School
                                
                                Основные возможности:
                                • Управление профилями пользователей
                                • Работа с навыками (создание, получение, предложения)
                                • Управление подписками пользователей
                                • Карьерное планирование
                                • Управление участием в событиях
                                • Массовая загрузка данных из CSV
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FAANG School Development Team")
                                .email("dev@faang.school")
                                .url("https://faang.school"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server")));
    }
}
