package school.faang.user_service.controller.premium;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.service.premium.PremiumService;

@Controller
@RequiredArgsConstructor
public class PremiumController {

    private final PremiumService service;

    public PremiumDto buyPremium(@NonNull final Long userId, @NonNull final Integer days) {
        return service.buyPremium(userId, days);
    }

    public void removePremium(@NonNull final Long userId) {
        service.removePremium(userId);
    }
}
