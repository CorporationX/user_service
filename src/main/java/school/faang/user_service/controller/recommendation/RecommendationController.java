package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequestMapping("api/v1/recommendation")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PutMapping
    public void giveRecommendation(RecommendationDto recommendation) {
        log.info("Получили запрос на получение рекомендаций.");
        recommendationService.create(recommendation); /// доделать
    }

    @DeleteMapping
    public void deleteRecommendation(long id) {
        log.info("Получили запрос на удаление рекомендации с ID: {}", id);
        recommendationService.
    }
}
