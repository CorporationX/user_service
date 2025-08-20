package school.faang.user_service.messaging.dto;

import java.time.LocalDateTime;

/**
 * Событие, фиксирующее появление пользователя в поиске.
 * <p>
 * Используется для передачи информации о том, что один пользователь
 * (searchedId) увидел другого пользователя (visitedId) в результатах поиска
 * в определённый момент времени.
 * </p>
 *
 * @param visitorId ID пользователя, который увидел другого в поиске
 * @param visitedId ID пользователя, который появился в поисковой выдаче
 * @param visitedAt время события появления
 * @author Myrza
 * @since 19.08.2025
 */
public record ProfileVisitEvent(
        Long visitorId,
        Long visitedId,
        LocalDateTime visitedAt
) {
}
