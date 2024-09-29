package school.faang.user_service.controller.premium;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequestMapping("/premuim")
@AllArgsConstructor
public class PremiumController {

    private final PremiumService premiumService;
    private final UserContext userContext;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PremiumDto buyPremium(@RequestBody @NonNull @Positive Integer days) {
        Long userId = userContext.getUserId();
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);

        return premiumService.buy(userId, premiumPeriod);
    }
}
