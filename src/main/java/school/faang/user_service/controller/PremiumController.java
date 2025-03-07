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
import school.faang.user_service.dto.PremiumActivated;
import school.faang.user_service.dto.PremiumRequest;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premium")
public class PremiumController {
    private final PremiumService premiumService;

    @GetMapping("/users/{id}")
    public PremiumActivated getPremiumForUserId(@NotNull @PathVariable("id") Long id) {
        return premiumService.getPremiumForUserId(id);
    }

    @PostMapping("/subscribe")
    public PremiumActivated subscribeToPremium(@NotNull @Valid @RequestBody PremiumRequest premiumRequest) {
        return premiumService.subscribeToPremium(premiumRequest);
    }
}
