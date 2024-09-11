package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

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
    public ResponseEntity<RecommendationDto> createRecommendation(
            @RequestBody @Valid RecommendationDto recommendationDto) {
        RecommendationDto createdRecommendation = recommendationService.create(recommendationDto);
        return ResponseEntity.status(201).body(createdRecommendation);
    }

    /**
     * Получение всех рекомендаций для пользователя
     */
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<RecommendationDto>> getAllUserRecommendations(
            @PathVariable long receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<RecommendationDto> recommendations = recommendationService.getAllUserRecommendations(
                receiverId, pageable);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Получение всех рекомендаций, которые дал пользователь (автор)
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(
            @PathVariable long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<RecommendationDto> recommendations = recommendationService.getAllGivenRecommendations(
                authorId, pageable);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Обновление рекомендации
     */
    @PutMapping("/{id}")
    public ResponseEntity<RecommendationDto> updateRecommendation(
            @PathVariable long id,
            @RequestBody @Valid RecommendationDto recommendationDto) {
        RecommendationDto updatedRecommendation = recommendationService.update(
                id, recommendationDto);
        return ResponseEntity.ok(updatedRecommendation);
    }

    /**
     * Удаление рекомендации
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}