package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.PremiumActivatedDto;
import school.faang.user_service.dto.PremiumRequestDto;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premium")
public class PremiumController {
    private final PremiumService premiumService;

    @GetMapping("/{userId}")
    public PremiumActivatedDto getPremiumForUserId(@NotNull @PathVariable("userId") Long userId) {
        return premiumService.getPremiumForUserId(userId);
    }

    @PostMapping("/subscribe")
    public PremiumActivatedDto subscribeToPremium(@NotNull @Valid @RequestBody PremiumRequestDto premiumRequestDto) {
        return premiumService.subscribeToPremium(premiumRequestDto);
    }
}
