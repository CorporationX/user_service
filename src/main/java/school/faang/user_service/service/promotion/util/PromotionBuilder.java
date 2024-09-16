package school.faang.user_service.service.promotion.util;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.time.LocalDateTime;

@Component
public class PromotionBuilder {

    public UserPromotion getUserPromotion(User user, PromotionTariff tariff) {
        return UserPromotion
                .builder()
                .promotionTariff(tariff)
                .cost(tariff.getCost())
                .currency(tariff.getCurrency())
                .coefficient(tariff.getCoefficient())
                .user(user)
                .numberOfViews(tariff.getNumberOfViews())
                .audienceReach(tariff.getAudienceReach())
                .creationDate(LocalDateTime.now())
                .build();
    }

    public EventPromotion getEventPromotion(Event event, PromotionTariff tariff) {
        return EventPromotion
                .builder()
                .promotionTariff(tariff)
                .cost(tariff.getCost())
                .currency(tariff.getCurrency())
                .coefficient(tariff.getCoefficient())
                .event(event)
                .numberOfViews(tariff.getNumberOfViews())
                .audienceReach(tariff.getAudienceReach())
                .creationDate(LocalDateTime.now())
                .build();
    }
}
