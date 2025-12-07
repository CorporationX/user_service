package school.faang.user_service.dto.premium;

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
public class PremiumBoughtEvent {
    private long userId;
    private BigDecimal paymentAmount;
    private int subscriptionDurationMonths;
    private LocalDateTime purchaseDateTime;
}