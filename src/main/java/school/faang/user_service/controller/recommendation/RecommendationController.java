package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Tag(name = "Управление рекомендациями")
@RestController
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "Дать рекомендацию")
    @PostMapping("/recommendations")
    public Long giveRecommendation(@RequestBody RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @Operation(summary = "Обновить рекомендацию пользователя по идентификатору")
    @PutMapping("/recommendations/{id}")
    public Recommendation updateRecommendation(@PathVariable Long id, @RequestBody RecommendationDto recommendation) {
        return recommendationService.update(recommendation);
    }

    @Operation(summary = "Удалить рекомендацию пользователя по идентификатору")
    @DeleteMapping("/recommendations/{id}")
    public void deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
    }

    @Operation(summary = "Получить рекомендации для пользователя по идентификатору")
    @GetMapping("recommendations/receiver/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable Long receiverId,
                                                             @RequestParam(value = "page") int page,
                                                             @RequestParam(value = "pageSize") int pageSize) {
        return recommendationService.getAllUserRecommendations(receiverId, page, pageSize);
    }

    @Operation(summary = "Получить рекомендации от пользователя по идентификатору")
    @GetMapping("/recommendations/author/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(@PathVariable Long authorId,
                                                              @RequestParam(value = "page") int page,
                                                              @RequestParam(value = "pageSize") int pageSize) {
        return recommendationService.getAllGivenRecommendations(authorId, page, pageSize);
    }
}
