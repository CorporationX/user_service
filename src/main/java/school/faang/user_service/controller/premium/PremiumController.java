package school.faang.user_service.controller.premium;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequestMapping("api/v1/premium")
@RequiredArgsConstructor
public class PremiumController {

    private final PremiumService premiumService;

    @PostMapping("/buy")
    public PremiumDto buyPremiumSubscription(@RequestHeader("x-user-id") Long userId, @RequestParam Integer days) {
       return premiumService.buyPremiumSubscription(userId, days);
    }

}
