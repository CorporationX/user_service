package school.faang.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.recommendation.RecommendationController;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;

import java.util.List;

@SpringBootApplication
@EnableFeignClients("school.faang.user_service.client")
@RequiredArgsConstructor
@Slf4j
public class UserServiceApplication implements CommandLineRunner {
    private final RecommendationController recommendationController;
    private final UserContext userContext;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        testRecommendationCreation();
        testRecommendationUpdate();
        testRecommendationDelete();
        testRecommendationFilter();
    }

    private void testRecommendationCreation() {
        long sessionUserId = 1;
        long recommendationRecipientUserId = 2;
        userContext.setUserId(sessionUserId);
        CreateRecommendationDto recommendationDto = new CreateRecommendationDto(
                recommendationRecipientUserId,
                "First ever recommendation"
        );
        RecommendationDto resultDto = recommendationController.create(recommendationDto);
        log.info("Result of creating recommendation: {}", resultDto);
    }

    private void testRecommendationUpdate() {
        long sessionUserId = 1;
        long recommendationId = 3;
        userContext.setUserId(sessionUserId);
        UpdateRecommendationDto recommendationDto = new UpdateRecommendationDto(
                "First ever recommendation updated"
        );
        RecommendationDto resultDto = recommendationController.update(recommendationId, recommendationDto);
        log.info("Result of updating recommendation: {}", resultDto);
    }

    private void testRecommendationDelete() {
        long sessionUserId = 1;
        long recommendationId = 3;
        userContext.setUserId(sessionUserId);
        recommendationController.delete(recommendationId);
        log.info("Recommendation id: {} should be deleted now 🤷", recommendationId);
    }

    private void testRecommendationFilter() {
        RecommendationFilterDto filterDto = new RecommendationFilterDto(
                "First",
                1L,
                2L
        );
        List<RecommendationDto> matches = recommendationController.getByFilters(filterDto);
        log.info("Filtered recommendation list: {} ", matches);
    }
}