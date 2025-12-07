package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequestMapping("/api/v1/premium")
@Validated
@RequiredArgsConstructor
public class PremiumController {

    private final PremiumService premiumService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<PremiumDto> buyPremium(@RequestParam("period") PremiumPeriod period) {
        long userId = userContext.getUserId();
        return ResponseEntity.ok(premiumService.buyPremium(userId, period));
    }
}
