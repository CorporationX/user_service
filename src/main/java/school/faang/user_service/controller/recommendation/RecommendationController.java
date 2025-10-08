package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Создает новую рекомендацию.
     *
     * @param recommendationDto данные для создания рекомендации
     * @return созданная рекомендация
     */
    @PostMapping
    public ResponseEntity<RecommendationDto> create(@Valid @RequestBody CreateRecommendationDto recommendationDto) {
        log.info("Creating recommendation for receiver: {}", recommendationDto.receiverId());

        validateCreateRecommendation(recommendationDto);

        RecommendationDto result = recommendationService.create(recommendationDto);
        return ResponseEntity.ok(result);
    }

    /**
     * Обновляет существующую рекомендацию.
     *
     * @param recommendationId идентификатор рекомендации
     * @param recommendationDto данные для обновления
     * @return обновленная рекомендация
     */
    @PutMapping("/{id}")
    public RecommendationDto update(@PathVariable("id") long recommendationId,
                                     @RequestBody UpdateRecommendationDto recommendationDto) {
        log.info("Updating recommendation: {}", recommendationId);
        
        validateUpdateRecommendation(recommendationDto);
        return recommendationService.update(recommendationId, recommendationDto);
    }

    /**
     * Удаляет рекомендацию.
     *
     * @param recommendationId идентификатор рекомендации
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long recommendationId) {
        log.info("Deleting recommendation: {}", recommendationId);
        recommendationService.delete(recommendationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает рекомендации по фильтрам.
     *
     * @param filters критерии фильтрации
     * @return список рекомендаций, соответствующих критериям
     */
    @PostMapping("/filter")
    public List<RecommendationDto> getByFilters(@RequestBody RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }

    private void validateCreateRecommendation(CreateRecommendationDto dto) {
        if (dto.receiverId() == null) {
            throw new DataValidationException("Receiver ID is required");
        }
        if (StringUtils.isBlank(dto.content())) {
            throw new DataValidationException("Content cannot be empty");
        }
    }

    private void validateUpdateRecommendation(UpdateRecommendationDto dto) {
        if (StringUtils.isBlank(dto.content())) {
            throw new DataValidationException("Content cannot be empty");
        }
    }
}
