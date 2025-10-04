package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;

import java.util.List;

/**
 * Сервис для управления рекомендациями.
 * Предоставляет методы для создания, обновления, удаления и получения рекомендаций.
 */
public interface RecommendationService {

    /**
     * Создаёт новую рекомендацию.
     * <p>
     * Условия:
     * <ul>
     *     <li>Пользователь не может написать рекомендацию сам себе</li>
     *     <li>Один пользователь может оставить рекомендацию другому не чаще одного раза в 6 месяцев</li>
     *     <li>Если в рекомендации предлагается навык, который уже есть у пользователя,
     *         автор рекомендации добавляется в гаранты этого навыка</li>
     * </ul>
     *
     * @param recommendationDto объект {@link CreateRecommendationDto}, содержащий данные для создания рекомендации
     * @return объект {@link RecommendationDto}, представляющий созданную рекомендацию
     */
    RecommendationDto create(CreateRecommendationDto recommendationDto);

    /**
     * Обновляет существующую рекомендацию.
     * <p>
     * Условия:
     * <ul>
     *     <li>Обновить рекомендацию может только автор рекомендации</li>
     * </ul>
     *
     * @param recommendationId идентификатор рекомендации
     * @param recommendationDto объект {@link UpdateRecommendationDto}, содержащий обновлённые данные
     * @return объект {@link RecommendationDto}, представляющий обновлённую рекомендацию
     */
    RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto);

    /**
     * Удаляет рекомендацию.
     * <p>
     * Условия:
     * <ul>
     *     <li>Удалить рекомендацию может только автор рекомендации</li>
     * </ul>
     *
     * @param recommendationId идентификатор рекомендации
     */
    void delete(long recommendationId);

    /**
     * Возвращает список рекомендаций, отфильтрованных по заданным критериям.
     *
     * @param filters объект {@link RecommendationFilterDto}, содержащий критерии фильтрации
     * @return список объектов {@link RecommendationDto}, соответствующих критериям фильтрации
     */
    List<RecommendationDto> getByFilters(RecommendationFilterDto filters);
}
