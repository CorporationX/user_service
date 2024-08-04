package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.client.paymentService.model.PaymentRequest;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.service.PromotionService;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@RestController
@RequestMapping("/promotion")
@RequiredArgsConstructor
@Validated
public class PromotionController {

    private final UserContext userContext;
    private final PromotionService service;

    @PostMapping("/user/buy")
    public PaymentRequest buyUserPromotion(@RequestParam long promotionId) {
        long userId = userContext.getUserId();
        return service.buyUserPromotion(userId, promotionId);
    }

    @PostMapping("/event/buy")
    public PaymentRequest buyEventPromotion(@RequestParam long eventId,
                                          @RequestParam long promotionId) {
        return service.buyEventPromotion(eventId, promotionId);
    }

    @GetMapping
    public List<PromotionDto> getAllPromotionTariffs() {
        return service.getAllPromotionTariffs();
    }
}
