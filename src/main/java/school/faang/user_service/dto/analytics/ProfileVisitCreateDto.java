package school.faang.user_service.dto.analytics;

import jakarta.validation.constraints.Positive;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * DTO для создания записи о посещении профиля.
 *
 * <p>Используется при сохранении информации о том, что один пользователь
 * (searchedId) посетил профиль другого пользователя (visitedId)
 * в определённый момент времени.</p>
 *
 * @param visitorId ID пользователя, который посетил чужой профиль
 * @param visitedId ID пользователя, чей профиль был посещён
 * @param visitedAt дата и время посещения
 * @author Myrza
 * @since 19.08.2025
 */
public record ProfileVisitCreateDto(
        @NonNull
        @Positive
        Long visitorId,
        @NonNull
        @Positive
        Long visitedId,
        @NonNull
        LocalDateTime visitedAt
) {
}
