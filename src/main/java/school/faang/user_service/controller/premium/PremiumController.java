package school.faang.user_service.controller.premium;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.service.premium.PremiumService;

@Validated
@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping
    public TransactionResultDto buyPremium(@RequestParam @NotNull @Positive Long userId,
                                           @RequestParam @NotNull @Positive Integer durationDays) {
        return premiumService.buyPremium(userId, durationDays);
    }
}
