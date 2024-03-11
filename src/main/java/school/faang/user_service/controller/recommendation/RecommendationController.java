package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recomendation.PageDto;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

@RestController
@RequestMapping("/api/v1/recommendation")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PutMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        log.info("Получен запрос на получение рекомендаций от пользователя с ID: {}.", recommendation.getAuthorId());
        return recommendationService.create(recommendation);
    }

    @PutMapping("/update")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto recommendation) {
        log.info("Получен запрос на обновление от пользователя с ID: {}", recommendation.getAuthorId());
        return recommendationService.update(recommendation);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable
                                     @Positive(message = "ID должно быть положительным число") long id) {
        log.info("Получен запрос на удаление рекомендации с ID: {}", id);
        recommendationService.delete(id);
    }

    @GetMapping("/receiver/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(
            @PathVariable @Positive(message = "ID должно быть положительным число") long receiverId,
            @Valid @ModelAttribute PageDto page) {
        log.info("Получен запрос на получение, пользователя с ID: {}", receiverId);
        return recommendationService.getAllUserRecommendations(receiverId, page);
    }

    @GetMapping("/author/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(
            @PathVariable @Positive(message = "ID должно быть положительным число") long authorId,
            @Valid @ModelAttribute PageDto page) {
        log.info("Получен запрос на получение рекомендаций пользователя с ID {}", authorId);
        return recommendationService.getAllGivenRecommendations(authorId, page);
    }
    @PostMapping()
    public RecommendationDto create(@RequestBody RecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }
}
