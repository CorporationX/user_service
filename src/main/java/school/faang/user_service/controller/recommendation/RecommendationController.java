package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    /**
     * Создание рекомендации
     */
    @PostMapping
    public RecommendationDto createRecommendation(@RequestBody @Valid RecommendationDto recommendationDto) {
        return recommendationService.createRecommendation(recommendationDto);
    }

    /**
     * Получение всех рекомендаций для пользователя
     */
    @GetMapping("/receiver/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(
            @PathVariable long receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recommendationService.getAllUserRecommendations(receiverId, pageable);
    }

    /**
     * Получение всех рекомендаций, которые дал пользователь (автор)
     */
    @GetMapping("/author/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(
            @PathVariable long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recommendationService.getAllGivenRecommendations(
                authorId, pageable);
    }

    /**
     * Обновление рекомендации
     */
    @PutMapping("/{id}")
    public RecommendationDto updateRecommendation(
            @PathVariable long id,
            @RequestBody @Valid RecommendationDto recommendationDto) {
        return recommendationService.updateRecommendation(
                id, recommendationDto);
    }

    /**
     * Удаление рекомендации
     */
    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.deleteRecommendation(id);
    }
}