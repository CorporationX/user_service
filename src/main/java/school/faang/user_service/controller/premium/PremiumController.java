package school.faang.user_service.controller.premium;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.BuyPremiumDto;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumPurchaseService;

@RestController
@RequestMapping("/v1/premuim")
@AllArgsConstructor
public class PremiumController {

    private final PremiumPurchaseService premiumPurchaseService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PremiumDto buyPremium(@RequestBody @NonNull @Positive BuyPremiumDto buyPremiumDto) {
        Long userId = userContext.getUserId();
        PremiumPeriod premiumPeriod = PremiumPeriod.getByDays(buyPremiumDto.getDays());

        return premiumPurchaseService.buy(userId, premiumPeriod);
    }
}
