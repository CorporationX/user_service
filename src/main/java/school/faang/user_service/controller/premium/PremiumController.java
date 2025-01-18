package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.service.premium.PremiumService;

@RequiredArgsConstructor
@RequestMapping("/premium")
@RestController
public class PremiumController {

    private final PremiumService premiumService;
    private final PremiumMapper premiumMapper;

    @PostMapping("/buy/{days}")
    public PremiumDto buyPremium(@PathVariable int days, @RequestHeader("x-user-id") long userId) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        Premium premium = premiumService.buyPremium(userId, premiumPeriod);
        return premiumMapper.toDto(premium);
    }
}
