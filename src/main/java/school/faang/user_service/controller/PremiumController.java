package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.entity.premium.PremiumPeriod;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/premium")
public class PremiumController {
    private final PremiumService premiumService;
    private final UserContext userContext;

    @PostMapping("/buy")
    public PremiumDto buyPremium(@RequestParam int months, @RequestParam long userId) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromMonths(months);
        userContext.setUserId(userId);
        PremiumDto result = premiumService.buyPremium(userId, premiumPeriod);
        userContext.clear();
        return result;
    }
}
