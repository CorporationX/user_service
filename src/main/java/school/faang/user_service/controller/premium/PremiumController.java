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
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.premium.PremiumCheckFailureException;
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
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);
        long userId = getContextUserId();
        Premium premium = premiumService.buyPremium(userId, premiumPeriod);
        return premiumMapper.toDto(premium);
    }

    private long getContextUserId() {
        try {
            return userContext.getUserId();
        } catch (IllegalArgumentException exception) {
            throw new PremiumCheckFailureException(exception.getMessage());
        }
    }
}
