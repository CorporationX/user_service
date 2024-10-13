package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.service.promotion.PromotionPurchaseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/promotion")
public class PromotionController {

    private final PromotionPurchaseService promotionPurchaseService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PromotionDto buyPromotion(@RequestParam PromotionType promotionType,
                                     @RequestParam PromotionTarget promotionTarget) {
        long userId = userContext.getUserId();

        return promotionPurchaseService.buyPromotion(userId, promotionType, promotionTarget);
    }
}
