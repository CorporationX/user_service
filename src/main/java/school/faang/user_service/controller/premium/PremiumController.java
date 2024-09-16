package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.service.premium.util.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.ResponsePremiumMapper;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.user.UserContextService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premiums")
public class PremiumController {
    private final PremiumService premiumService;
    private final ResponsePremiumMapper premiumMapper;
    private final UserContextService userContextService;

    @PostMapping("/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponsePremiumDto buyPremium(@RequestParam(name = "days") int days) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        long userId = userContextService.getContextUserId();
        Premium premium = premiumService.buyPremium(userId, premiumPeriod);
        return premiumMapper.toDto(premium);
    }
}
