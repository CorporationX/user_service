package school.faang.user_service.dto.premium;

import lombok.Builder;
import lombok.Value;
import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PremiumPeriod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class PremiumDto {
    long id;
    long userId;
    PremiumPeriod premiumPeriod;
    LocalDateTime startDate;
    LocalDateTime endDate;
    BigDecimal amount;
    String paymentNumber;
    int verificationCode;
    Currency currency;
    LocalDateTime createdAt;
}
