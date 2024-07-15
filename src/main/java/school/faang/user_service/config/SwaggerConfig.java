package school.faang.user_service.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
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
                        .title("FAANG User Service API")
                        .version("1.0")
                        .description("API для управления пользователями и рекомендациями в системе FAANG")
                        .contact(new Contact()
                                .name("Your Name")
                                .url("https://www.yourwebsite.com")
                                .email("your-email@yourwebsite.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("FAANG User Service Documentation")
                        .url("https://www.yourwebsite.com/docs"));
    }
//
//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("faang-public")
//                .pathsToMatch("/recommendation/**")
//                .build();
//    }
}
