package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumResponseDto {
    private String status;
    private int verificationCode;
    private Long paymentNumber;
    private BigDecimal amount;
    private String currency;
    private String message;
    private PremiumDto tariffPlan;
}