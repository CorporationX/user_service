package school.faang.user_service.dto.premium;

import lombok.Getter;

@Getter
public class PremiumRequestDto {
    private int months;
    private long userId;
    private long paymentNumber;
}
