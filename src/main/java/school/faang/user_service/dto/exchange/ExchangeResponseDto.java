package school.faang.user_service.dto.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.payment.CurrencyDto;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeResponseDto {
    private CurrencyDto currency;
    private BigDecimal amount;
    private Long userId;
}
