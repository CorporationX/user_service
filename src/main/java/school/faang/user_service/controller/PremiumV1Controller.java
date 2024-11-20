package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/premium/")
@RestController
public class PremiumV1Controller {
    private final PremiumService premiumService;

    @PostMapping("buy/{userId}")
    public PremiumDto buyPremium(@Positive @RequestParam int days,
                                 @Positive @PathVariable long userId) {
        PremiumPeriod premPeriod = PremiumPeriod.fromDays(days);
        return premiumService.buyPremium(premPeriod, userId);
    }
}
