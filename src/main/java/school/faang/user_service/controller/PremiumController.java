package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.service.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premium")
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("/{days}")
    public PremiumDto buyPremium(@RequestHeader(value = "x-user-id") long userId, @PathVariable int days) {
        return premiumService.buyPremium(userId, days);
    }
}
