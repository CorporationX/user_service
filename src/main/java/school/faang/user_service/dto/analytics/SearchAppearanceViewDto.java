package school.faang.user_service.dto.analytics;

import java.time.LocalDateTime;

/**
 * SearchAppearanceViewDto — DTO для отображения информации о поисковых появлениях пользователя.
 * <p>
 * Используется в REST-эндпоинтах для возврата клиенту информации о том,
 * кто и когда искал конкретного пользователя.
 * </p>
 *
 * @param id         уникальный идентификатор записи в базе
 * @param searcherId идентификатор пользователя, который выполнял поиск
 * @param searchedId идентификатор пользователя, которого нашли
 * @param searchedAt дата и время, когда произошло появление в поиске
 * @author Myrza
 * @since 20.08.2025
 */
public record SearchAppearanceViewDto(
        Long id,
        Long searcherId,
        Long searchedId,
        LocalDateTime searchedAt

) {
}
