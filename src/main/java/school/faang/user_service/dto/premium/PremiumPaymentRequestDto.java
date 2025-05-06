package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentRequestDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumPaymentRequestDto {
    private PremiumRequestDto premiumRequest;
    private PaymentRequestDto paymentRequest;
    private CurrencyDto selectedCurrency;
    private boolean byUser;
}
