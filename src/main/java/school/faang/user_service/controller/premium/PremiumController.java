package school.faang.user_service.controller.premium;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PremiumController {

    private final UserContext userContext;
    private final PremiumServiceImpl premiumService;

    @PostMapping("/premium")
    public PremiumDto buyPremium(@RequestParam @Positive int days) {
        var period = PremiumPeriod.fromDays(days);
        return premiumService.buyPremium(userContext.getUserId(), period);
    }
}