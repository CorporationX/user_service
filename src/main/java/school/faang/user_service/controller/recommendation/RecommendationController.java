package school.faang.user_service.controller.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("api/v1/recommendation")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PutMapping
    public void giveRecommendation(RecommendationDto recommendation) {
        log.info("Получили запрос на получение рекомендаций.");
        recommendationService.create(recommendation); /// доделать
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable
                                     @Positive(message = "ID должно быть положительным число") long id) {
        log.info("Получили запрос на удаление рекомендации с ID: {}", id);
        recommendationService.delete(id);
    }

    @GetMapping("/{recieverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable
                                                             @Positive(message = "ID должно быть положительным число")
                                                             long recieverId) {
        log.info("Получили запрос на получение всех рекомендаций, пользователя с ID: {}", recieverId);
        return recommendationService.getAllUserRecommendations(recieverId);


    }
}
