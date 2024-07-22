package school.faang.user_service.dto.premium;

import java.time.LocalDateTime;

public record PremiumDto(
        long id,
        long userId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
