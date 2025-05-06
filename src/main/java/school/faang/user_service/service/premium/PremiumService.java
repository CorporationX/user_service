package school.faang.user_service.service.premium;

import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;

public interface PremiumService {
    PremiumResponseDto buyPremium(PremiumRequestDto premiumRequestDto, boolean byUser);

    ExchangeResponseDto getPremiumPrice(PremiumRequestDto premiumRequestDto);

    void updateAutoRenew(boolean autoRenew, Long userId);

    void premiumRenewal();
}
