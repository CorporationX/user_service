package school.faang.user_service.controller.premium;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.validation.ObjectValidator;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;
    private final ObjectValidator objectValidator;

    @PostMapping("/premium")
    public PremiumResponseDto buyPremium(@RequestBody PremiumRequestDto premiumRequestDto) {
        objectValidator.validate(premiumRequestDto);
        return premiumService.buyPremium(premiumRequestDto);
    }
}
