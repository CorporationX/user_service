package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPremiumBoughtEvent {

    private long userId;
    private BigDecimal amount;
    private int premiumPeriod;
    private LocalDateTime timestamp;
}
