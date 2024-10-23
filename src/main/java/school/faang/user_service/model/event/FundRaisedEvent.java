package school.faang.user_service.model.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record FundRaisedEvent(
        long userId,
        long projectId,
        BigDecimal amount,
        LocalDateTime donatedAt
) {
}
