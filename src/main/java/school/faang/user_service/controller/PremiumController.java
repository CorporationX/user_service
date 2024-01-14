package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@RestController
@RequestMapping("/api/premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("/buy/{id}")
    public PremiumDto buyPremium(@PathVariable("id") long id, @RequestParam("day") int day) {
        PremiumPeriod period = PremiumPeriod.fromDays(day);
        return premiumService.buyPremium(id, period);
    }
}
