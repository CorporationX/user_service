package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premiun.PremiumDto;
import school.faang.user_service.dto.premiun.PremiumRequest;
import school.faang.user_service.service.PremiumService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/premium")
public class PremiumController {

    private final PremiumService premiumService;

    @PostMapping("/buy/{id}")
    public PremiumDto buyPremium(@PathVariable long id, @RequestBody @Valid PremiumRequest request) {
        return premiumService.buyPremium(request, id);
    }
}
