package school.faang.user_service.controller.donate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.DonateService;

/**
 * Заглушка
 */
@RestController
@RequestMapping("/api/v1/donates")
@RequiredArgsConstructor
public class DonateController {
    private final DonateService donateService;

    @PostMapping
    public ResponseEntity<String> donate() {
        donateService.donate();
        return ResponseEntity.ok("{\"message\":\"success\"}");
    }
}
