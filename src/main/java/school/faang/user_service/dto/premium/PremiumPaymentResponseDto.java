package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.payment.PaymentResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumPaymentResponseDto {
    private PremiumRequestDto premiumRequest;
    private PaymentResponseDto paymentResponse;
    private boolean byUser;
}
