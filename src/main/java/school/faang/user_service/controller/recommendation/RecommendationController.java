package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Создает новую рекомендацию.
     *
     * @param recommendationDto данные для создания рекомендации
     * @return созданная рекомендация
     */
    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        log.info("Creating recommendation for receiver: {}", recommendationDto.receiverId());
        
        validateCreateRecommendation(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    /**
     * Обновляет существующую рекомендацию.
     *
     * @param recommendationId идентификатор рекомендации
     * @param recommendationDto данные для обновления
     * @return обновленная рекомендация
     */
    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        log.info("Updating recommendation: {}", recommendationId);
        
        validateUpdateRecommendation(recommendationDto);
        return recommendationService.update(recommendationId, recommendationDto);
    }

    /**
     * Удаляет рекомендацию.
     *
     * @param recommendationId идентификатор рекомендации
     */
    public void delete(long recommendationId) {
        log.info("Deleting recommendation: {}", recommendationId);
        recommendationService.delete(recommendationId);
    }

    /**
     * Получает рекомендации по фильтрам.
     *
     * @param filters критерии фильтрации
     * @return список рекомендаций, соответствующих критериям
     */
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        log.info("Getting recommendations with filters: {}", filters);
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
