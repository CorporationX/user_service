package school.faang.user_service.config.openApi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApt(){
        return new  OpenAPI()
                .info(new Info()
                        .title("user_service")
                        .description("Микросервис для работы с пользователями")
                        .version("v0.1"));
    }
}
