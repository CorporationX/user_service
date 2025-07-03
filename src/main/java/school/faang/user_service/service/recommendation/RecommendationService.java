package school.faang.user_service.service.recommendation;

import jakarta.transaction.Transactional;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;

import java.util.List;

/**
 *
 */
public interface RecommendationService {

    /**
     * Создаёт новую рекоммендацию на основе переданных данных.
     * <p>
     * Условия:
     * <ul>
     *     <li>Один пользователь другому может оставлять рекомендацию не чаще одного раза
     *     в 6 месяцев, при нарушении выбрасывается {@code DataValidationException}.</li>
     *     <li>Пользователь не может написать рекомендацию сам себе
     *         при нарушении выбрасывается {@code ForbiddenException}.</li>
     * </ul>
     *
     * @param recommendationDto объект {@link CreateRecommendationDto},
     *                          содержащий информацию для создания рекомендации
     * @return объект {@link RecommendationDto}, представляющий созданную рекомендацию
     */
    RecommendationDto create(CreateRecommendationDto recommendationDto);

    /**
     * Обновляет существующую рекомендацию.
     * <p>
     * Условия:
     * <ul>
     *     <li>Рекомендация с указанным {@code recommendationId} должна существовать —
     *         иначе выбрасывается {@code EntityNotFoundException}.</li>
     *     <li>Обновление данных рекомендации созданной другим пользователем не допускается —
     *         в этом случае выбрасывается {@code ForbiddenException}.</li>
     * </ul>
     *
     * @param recommendationId  идентификатор рекомендации, чьи данные необходимо обновить
     * @param recommendationDto объект {@link UpdateRecommendationDto}, содержащий обновлённые данные рекомендации
     * @return объект {@link RecommendationDto}, представляющий обновлённую рекомендацию
     */
    RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto);

    /**
     * Удаляет рекомендацию из системы по ее идентификатору.
     * <p>
     * Условия:
     * <ul>
     *     <li>Рекомендация с указанным {@code recommendationId} должна существовать —
     *         иначе выбрасывается {@code EntityNotFoundException}.</li>
     *     <li>Удаление рекомендации созданной другим пользователем не допускается —
     *         в этом случае выбрасывается {@code ForbiddenException}.</li>
     * </ul>
     * Если рекомендация с указанным идентификатором не найдена,
     * выбрасывается {@code EntityNotFoundException}.
     *
     * @param recommendationId идентификатор пользователя
     */
    @Transactional
    void delete(long recommendationId);

    /**
     * Возвращает рекомендации соответствующие фильтрам, или все рекомендации если фильтры пустые
     *
     * @param filters объект содержащий фильтры для применения со значениями
     * @return список {@link List} рекомендаций, соответствующих фильтрам
     */
    List<RecommendationDto> getByFilters(RecommendationFilterDto filters);
}


