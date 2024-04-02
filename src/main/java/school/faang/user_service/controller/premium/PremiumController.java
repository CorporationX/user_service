package school.faang.user_service.controller.premium;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.validators.UserValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PremiumController {
    private final PremiumService premiumService;
    private final UserContext userContext;
    private final UserValidator userValidator;

    @PatchMapping("/premium/{days}")
    public PremiumDto buyPremium(@Min(30) @PathVariable int days) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        long userId = userContext.getUserId();
        userValidator.userExistenceInRepo(userId);
        return premiumService.buyPremium(userId, premiumPeriod);
    }
}
