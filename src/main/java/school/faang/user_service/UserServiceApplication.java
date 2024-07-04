package school.faang.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.support.TransactionTemplate;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

@SpringBootApplication
@EnableFeignClients("school.faang.user_service.client")
@OpenAPIDefinition(
        info = @Info(
                title = "User Service",
                version = "1.0.0")
)
public class UserServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(UserServiceApplication.class, args);
        context.getBean(TransactionTemplate.class).executeWithoutResult(tx -> {
            context.getBean(EventService.class).findAll(new EventFilterDto(
                            "title"
                    )).stream()
                    .map(EventReadDto::getTitle)
                    .forEach(System.out::println);
        });
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}