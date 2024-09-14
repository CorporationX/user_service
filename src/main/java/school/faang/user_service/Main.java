package school.faang.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(UserServiceApplication.class, args);
        RecommendationController controller = context.getBean(RecommendationController.class);
        SkillOfferDto skillOfferDto1 = new SkillOfferDto(11L, 2L);
        SkillOfferDto skillOfferDto2 = new SkillOfferDto(12L, 1L);
        List<SkillOfferDto> skillOfferDtos = List.of(skillOfferDto1, skillOfferDto2);
        RecommendationDto dto = new RecommendationDto(1L, 1L, 2L, "obnova",
                skillOfferDtos, LocalDateTime.now());

        controller.deleteRecommendation(1L);

        context.close();
    }
}
