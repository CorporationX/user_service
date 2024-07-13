package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.PromotionDto;
import school.faang.user_service.dto.PromotionalPlan;
import school.faang.user_service.entity.promotion.AudienceReach;
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
        @RequestParam int impressions,
        @RequestParam String audienceReach
    ) {
        return promotionService.promoteUser(
            userId,
            PromotionalPlan.getPromotionalPlan(impressions, AudienceReach.fromName(audienceReach))
        );
    }

    @PostMapping("/event/promote/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public PromotionDto promoteEvent(
        @PathVariable long eventId,
        @RequestParam int impressions,
        @RequestParam String audienceReach
    ) {
        return promotionService.promoteEvent(
            eventId,
            PromotionalPlan.getPromotionalPlan(impressions, AudienceReach.fromName(audienceReach))
        );
    }
}
