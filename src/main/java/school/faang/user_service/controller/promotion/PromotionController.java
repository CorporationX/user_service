package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.service.promotion.PromotionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotion")
public class PromotionController {

    private final PromotionService promotionService;
    private final UserContext userContext;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionDto buyPromotion(@RequestParam(required = true) PromotionType promotionType, @RequestParam(required = true) PromotionTarget promotionTarget) {
        long userId = userContext.getUserId();

        return promotionService.buyPromotion(userId, promotionType, promotionTarget);
    }
}
