package school.faang.user_service.service.analytics;

import school.faang.user_service.dto.analytics.SearchAppearanceCreateDto;
import school.faang.user_service.dto.analytics.SearchAppearanceViewDto;

import java.util.List;

/**
 * Сервис для работы с аналитикой поисковых появлений пользователя.
 * <p>
 * Определяет контракт для операций над сущностью {@code SearchAppearance}:
 * <ul>
 *   <li>{@link #addSearchAppearance(SearchAppearanceCreateDto)} —
 *       добавление информации о том, что профиль пользователя появился в поиске</li>
 *   <li>{@link #getUserSearchAppearance(Long, int, int)} —
 *       получение списка поисковых появлений конкретного пользователя
 *       с поддержкой пагинации</li>
 * </ul>
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
public interface SearchAppearanceService {
    void addSearchAppearance(SearchAppearanceCreateDto createDto);

    List<SearchAppearanceViewDto> getUserSearchAppearance(Long searchedId, int limit, int page);
}
