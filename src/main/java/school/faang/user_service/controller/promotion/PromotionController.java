package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.ResponseEventDto;
import school.faang.user_service.dto.promotion.ResponseEventPromotionDto;
import school.faang.user_service.dto.promotion.ResponseUserDto;
import school.faang.user_service.dto.promotion.ResponseUserPromotionDto;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.mapper.promotion.ResponseEventPromotionMapper;
import school.faang.user_service.mapper.promotion.ResponseEventMapper;
import school.faang.user_service.mapper.promotion.ResponseUserMapper;
import school.faang.user_service.mapper.promotion.ResponseUserPromotionMapper;
import school.faang.user_service.service.promotion.PromotionService;
import school.faang.user_service.service.user.UserContextService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotions")
public class PromotionController {
    private final PromotionService promotionService;
    private final UserContextService userContextService;
    private final ResponseUserPromotionMapper userPromotionMapper;
    private final ResponseEventPromotionMapper eventPromotionMapper;
    private final ResponseUserMapper responseUserMapper;
    private final ResponseEventMapper responseEventMapper;

    @PostMapping("/buy")
    public ResponseUserPromotionDto buyPromotion(@RequestParam(name = "views") int numberOfViews) {
        PromotionTariff tariff = PromotionTariff.fromViews(numberOfViews);
        long userId = userContextService.getContextUserId();
        UserPromotion userPromotion = promotionService.buyPromotion(userId, tariff);
        return userPromotionMapper.toDto(userPromotion);
    }

    @PostMapping("/events/{id}/buy")
    public ResponseEventPromotionDto buyEventPromotion(@PathVariable(name = "id") long eventId,
                                                       @RequestParam(name = "views") int numberOfViews) {
        PromotionTariff tariff = PromotionTariff.fromViews(numberOfViews);
        long userId = userContextService.getContextUserId();
        EventPromotion eventPromotion = promotionService.buyEventPromotion(userId, eventId, tariff);
        return eventPromotionMapper.toDto(eventPromotion);
    }

    @GetMapping("/per-page")
    public List<ResponseUserDto> getPromotedUsersBeforeAllPerPage(@RequestParam(name = "limit") int limit,
                                                                  @RequestParam(name = "offset") int offset) {
        return promotionService.getPromotedUsersBeforeAllPerPage(limit, offset)
                .stream()
                .map(responseUserMapper::toDto)
                .toList();
    }

    @GetMapping("/events/per-page")
    public List<ResponseEventDto> getPromotedEventsBeforeAllPerPage(@RequestParam(name = "limit") int limit,
                                                                    @RequestParam(name = "offset") int offset) {
        return promotionService.getPromotedEventsBeforeAllPerPage(limit, offset)
                .stream()
                .map(responseEventMapper::toDto)
                .toList();
    }
}
