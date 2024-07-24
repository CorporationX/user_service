package school.faang.user_service.controller.premium;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.model.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

/**
 * @author Evgenii Malkov
 */

@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
@Validated
public class PremiumController {

    private final UserContext userContext;
    private final PremiumService premiumService;

    @PostMapping
    public long buyPremium(@RequestParam @Positive int days) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        return premiumService.buyPremium(userContext.getUserId(), premiumPeriod);
    }
}
