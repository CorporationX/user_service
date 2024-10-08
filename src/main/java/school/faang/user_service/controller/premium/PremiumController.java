package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.BuyPremiumDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.user.UserContextService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premiums")
public class PremiumController {
    private final PremiumService premiumService;
    private final PremiumMapper premiumMapper;
    private final UserContextService userContextService;

    @PostMapping("/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public PremiumResponseDto buyPremium(@RequestBody BuyPremiumDto buyPremiumDto) {
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(buyPremiumDto.days());
        long userId = userContextService.getContextUserId();
        Premium premium = premiumService.buyPremium(userId, premiumPeriod);
        return premiumMapper.toPremiumResponseDto(premium);
    }
}
