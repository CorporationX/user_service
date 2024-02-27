package school.faang.user_service.controller.recommendation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

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
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable
                                                             @Positive(message = "ID должно быть положительным число")
                                                             long recieverId,
                                                             @RequestParam(value = "page", defaultValue = "0")
                                                             @Min(value = 0, message = "Page не может быть меньше 0")
                                                             int page,
                                                             @RequestParam(value = "pageSize", defaultValue = "10")
                                                             @Min(value = 1, message = "PageSize должно быть > 0")
                                                             int pageSize) {
        log.info("Получили запрос на получение всех рекомендаций, пользователя с ID: {}", recieverId);
        return recommendationService.getAllUserRecommendations(recieverId, page, pageSize);


    }
}
