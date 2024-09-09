package school.faang.user_service.dto.premium;

import java.time.LocalDateTime;

public record ResponsePremiumDto(
        Long id,
        Long userId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
