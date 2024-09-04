package school.faang.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.PremiumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/premium")
public class PremiumController {
    private final PremiumService service;
    @PostMapping
    public void buyPremium() throws JsonProcessingException {
        service.buyPremium();
    }
}