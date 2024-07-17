package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.service.PromotionService;

@RestController
@RequestMapping("promotion")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @PostMapping("/user/promote/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionDto promoteUser(
        @PathVariable long userId,
        @RequestParam String promotionalPlan,
        @RequestParam String currency
    ) {
        return promotionService.promoteUser(userId, promotionalPlan, currency);
    }

    @PostMapping("/event/promote/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionDto promoteEvent(
        @PathVariable long eventId,
        @RequestParam String promotionalPlan,
        @RequestParam String currency
    ) {
        return promotionService.promoteEvent(eventId, promotionalPlan, currency);
    }
}
