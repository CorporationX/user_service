package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.PromotionDto;
import school.faang.user_service.model.enums.PromotionType;
import school.faang.user_service.service.PromotionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotion")
public class PromotionController {
    private final PromotionService promotionService;
    private final UserContext userContext;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PromotionDto buyPromotion(@RequestParam PromotionType type,
                                     @RequestParam String target) {
        long userId = userContext.getUserId();
        return promotionService.buyPromotion(userId, type, target);
    }
}
