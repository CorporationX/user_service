package school.faang.user_service.service.recommendation;

import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationResponseDto;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequestDto;

import java.util.List;

/**
 * Сервисный интерфейс для управления пользовательскими рекомендациями.
 * <p>
 * Отвечает за создание, обновление, удаление и получение рекомендаций по заданным критериям.
 * Реализации должны обеспечивать необходимую валидацию данных и проверку прав доступа.
 */
public interface RecommendationService {

    /**
     * Создает новую рекомендацию от текущего аутентифицированного автора указанному получателю.
     * <p>
     * Ожидаемые правила (не исчерпывающе):
     * - должен быть указан receiverId
     * - текст рекомендации (content) не должен быть пустым
     * - автор не может рекомендовать сам себя
     * - может действовать ограничение по времени (кулдаун) на повторные рекомендации одному и тому же получателю
     * <p>
     * Должна возвращаться полная DTO созданной рекомендации.
     *
     * @param recommendationDto данные для создания рекомендации (получатель, содержимое, необязательные skillIds)
     * @return созданная рекомендация
     */
    RecommendationResponseDto create(CreateRecommendationRequestDto recommendationDto);

    /**
     * Обновляет существующую рекомендацию по ее идентификатору.
     * <p>
     * Типичное поведение:
     * - операция доступна только авторизованным пользователям (например, исходному автору)
     * - входные поля валидируются (например, content не должен быть пустым, если передан)
     * - при поддержке со стороны DTO могут обновляться связанные атрибуты (например, список скиллов)
     *
     * @param recommendationId  идентификатор обновляемой рекомендации
     * @param recommendationDto поля для обновления
     * @return обновленная рекомендация
     */
    RecommendationResponseDto update(long recommendationId, UpdateRecommendationRequestDto recommendationDto);

    /**
     * Удаляет рекомендацию по ее идентификатору.
     * <p>
     * Типичное поведение:
     * - операция доступна только авторизованным пользователям (например, исходному автору или ролям с особыми правами)
     * - поведение при повторном удалении или отсутствии сущности зависит от реализации (исключение или no-op)
     *
     * @param recommendationId идентификатор удаляемой рекомендации
     */
    void delete(long recommendationId);

    /**
     * Возвращает список рекомендаций, удовлетворяющих переданным фильтрам.
     * <p>
     * Рекомендации к реализации:
     * - отсутствующие поля фильтра трактуются как «любой»
     * - при отсутствии совпадений возвращается пустой список
     * - опционально могут поддерживаться сортировка и пагинация, если это предусмотрено фильтром
     *
     * @param filters критерии фильтрации (например, автор, получатель, диапазон дат, скиллы)
     * @return список рекомендаций, удовлетворяющих фильтрам (возможно пустой)
     */
    List<RecommendationResponseDto> getByFilters(FilterRecommendationRequestDto filters);
}
