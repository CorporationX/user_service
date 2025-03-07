package school.faang.user_service.dto;

import java.time.LocalDateTime;

public record PremiumActivated(LocalDateTime startDate, LocalDateTime endDate) {
}
