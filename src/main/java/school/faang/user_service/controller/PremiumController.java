package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.entity.premium.PremiumPeriod;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/premium")
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping("/buy")
    public PremiumDto buyPremium(@RequestBody PremiumRequestDto premiumRequestDto) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromMonths(premiumRequestDto.getMonths());
        return premiumService.buyPremium(
                premiumRequestDto.getUserId(),
                premiumRequestDto.getPaymentNumber(),
                premiumPeriod);
    }
}
