package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@Tag(name = "Премиум контролер")
@RestController
@RequestMapping("/api/premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;
    private final UserContext userContext;

    @Operation(
            summary = "Покупка премиума",
            description = "Позволяет купить премиум по тарифному плану",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "id пользователя", required = true)}
    )
    @PostMapping("/buy")
    public PremiumDto buyPremium(@RequestParam("day") int day) {
        PremiumPeriod period = PremiumPeriod.fromDays(day);
        long id = userContext.getUserId();
        return premiumService.buyPremium(id, period);
    }
}
