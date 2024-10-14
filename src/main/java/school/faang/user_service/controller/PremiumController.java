package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.PremiumDto;
import school.faang.user_service.model.enums.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;
    private final UserContext userContext;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PremiumDto buyPremium(@RequestParam(name = "days") int days) {
        PremiumPeriod period = PremiumPeriod.fromDays(days);
        long userId = userContext.getUserId();
        return premiumService.buyPremium(userId, period);
    }
}
