package school.faang.user_service.dto.analytics;

import java.time.LocalDateTime;

/**
 * DTO для представления факта посещения профиля другим пользователем.
 * <p>
 * Используется в API аналитики для возврата информации о визитах к профилю.
 * </p>
 *
 * @param id        уникальный идентификатор события посещения
 * @param visitorId идентификатор пользователя, который посетил профиль
 * @param visitedId идентификатор пользователя, чей профиль был посещён
 * @param visitedAt дата и время визита
 * @author Myrza
 * @since 19.08.2025
 */
public record ProfileVisitViewDto(
        Long id,
        Long visitorId,
        Long visitedId,
        LocalDateTime visitedAt
) {
}
