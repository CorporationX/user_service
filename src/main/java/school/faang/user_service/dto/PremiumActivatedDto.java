package school.faang.user_service.dto;

import java.time.LocalDateTime;

public record PremiumActivatedDto(LocalDateTime startDate, LocalDateTime endDate) {
}
