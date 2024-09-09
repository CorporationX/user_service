package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premiums")
public class PremiumController {
    private final PremiumService premiumService;
    private final PremiumMapper premiumMapper;
    private final UserContext userContext;

    @PostMapping("/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponsePremiumDto buyPremium(@RequestParam(name = "days") int days) {
        var premiumPeriod = PremiumPeriod.fromDays(days);
        var premium = premiumService.buyPremium(userContext.getUserId(), premiumPeriod);
        return premiumMapper.toDto(premium);
    }
}
