package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.service.PremiumService;

@RestController
@RequestMapping("premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("/buyPremium/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public PremiumDto buyPremium(
        @PathVariable long userId,
        @RequestParam int days,
        @RequestParam String currency
    ) {
        return premiumService.buyPremium(userId, days, currency);
    }
}
