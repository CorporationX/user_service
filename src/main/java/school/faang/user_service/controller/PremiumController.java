package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/premium")
public class PremiumController {
    private final PremiumService service;

    @PostMapping("/buy")
    public PremiumDto buyPremium(@RequestHeader("x-user-id") long id, @RequestParam int days) {
        return service.buyPremium(id, PremiumPeriod.fromDays(days));
    }
}
