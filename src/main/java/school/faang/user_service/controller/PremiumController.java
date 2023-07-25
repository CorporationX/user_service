package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
public class PremiumController {

    private final PremiumService premiumService;

    @PostMapping("/buy/{userId}")
    public PremiumDto buyPremium(@PathVariable long userId, @RequestParam int days) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        return premiumService.buyPremium(userId, premiumPeriod);
    }
}
