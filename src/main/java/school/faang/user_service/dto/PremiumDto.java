package school.faang.user_service.dto;

import java.time.LocalDateTime;

public record PremiumDto(
    Long id,
    long userId,
    LocalDateTime startDate,
    LocalDateTime endDate
) {
}
