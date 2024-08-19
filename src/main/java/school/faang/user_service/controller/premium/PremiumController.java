package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premium")
public class PremiumController {
    private final PremiumService service;

    @PostMapping("/buy")
    public PremiumDto buyPremium(@RequestHeader("x-user-id") long id, @RequestParam int days) {
        return service.buyPremium(id, PremiumPeriod.fromDays(days));
    }
}
