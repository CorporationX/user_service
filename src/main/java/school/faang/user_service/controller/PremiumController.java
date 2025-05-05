package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.exchange.ExchangeResponseDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.service.premium.PremiumService;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping
    public PremiumResponseDto buyPremium(@RequestBody @Valid PremiumRequestDto premiumRequestDto) {
        log.info("Received request to purchase premium for user with ID {}", premiumRequestDto.getUserId());
        return premiumService.buyPremium(premiumRequestDto, true);
    }

    @GetMapping("/price")
    public ExchangeResponseDto getPremiumPrice(@RequestBody @Valid PremiumRequestDto premiumRequestDto) {
        log.info("Received request to get premium price in {}", premiumRequestDto.getSelectedCurrency());
        return premiumService.getPremiumPrice(premiumRequestDto);
    }

    @PutMapping("/user/{userId}")
    public void updateAutoRenew(@PathVariable @NotNull @Positive Long userId,
                                @RequestParam boolean autoRenew) {
        log.info("Received request to set premium auto renew = {} for user with ID {}",
                autoRenew, userId);
        premiumService.updateAutoRenew(autoRenew, userId);
    }
}
