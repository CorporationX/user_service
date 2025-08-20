package school.faang.user_service.dto.analytics;

import jakarta.validation.constraints.Positive;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * DTO для создания события {@code SearchAppearance}.
 * <p>
 * Используется при сохранении информации о том, что профиль одного пользователя
 * был показан другому в результатах поиска.
 * </p>
 * Валидация:
 * <ul>
 *   <li>{@code searcherId} и {@code searchedId} должны быть положительными значениями</li>
 *   <li>Все поля обязательны для заполнения (аннотированы {@link lombok.NonNull})</li>
 * </ul>
 *
 * @param searcherId идентификатор пользователя, который выполнял поиск
 * @param searchedId идентификатор пользователя, чей профиль появился в результатах поиска
 * @param searchedAt дата и время, когда произошло появление профиля в поиске *
 * @author Myrza
 * @since 19.08.2025
 */
public record SearchAppearanceCreateDto(
        @NonNull
        @Positive
        Long searcherId,
        @NonNull
        @Positive
        Long searchedId,
        @NonNull
        LocalDateTime searchedAt
) {
}
