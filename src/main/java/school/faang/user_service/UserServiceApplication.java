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
import school.faang.user_service.controller.recommendation.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;

import java.util.List;

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

//        RecommendationController controller = context.getBean(RecommendationController.class);
//        RecommendationDto recommendation = new RecommendationDto();
//        recommendation.setAuthorId(1L);
//        recommendation.setContent("Привет");
//        recommendation.setReceiverId(2L);
//        recommendation.setSkillOffers(
//                List.of(
//                        SkillOfferDto.builder()
//                                .skillId(3L)
//                                .recommendationId(2L)
//                                .build()
//                )
//        );
//
//        System.out.println(controller.giveRecommendation(recommendation));
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}