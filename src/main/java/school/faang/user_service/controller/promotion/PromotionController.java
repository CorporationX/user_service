package school.faang.user_service.controller.promotion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.service.promotion.interfaces.PromotionService;

@RestController
@RequestMapping("/promotions")
@Slf4j
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<Void> addPromotion(@Valid @RequestBody PromotionDto promotionDto) {
        log.info("Request received: method=POST, URI=/promotions, promotionDto={}", promotionDto);
        promotionService.addPromotion(promotionDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
