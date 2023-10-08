package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
@Slf4j
public class PremiumController {

    private final PremiumService premiumService;
    private final UserContext userContext;

    @PostMapping("/buy")
    public PremiumDto buyPremium(@RequestParam("period") int days) {
        Long userId = userContext.getUserId();
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);

        log.info("Received request to buy premium: {}, for user with id {}", premiumPeriod, userId);
        return premiumService.buyPremium(userId, premiumPeriod);
    }
}
