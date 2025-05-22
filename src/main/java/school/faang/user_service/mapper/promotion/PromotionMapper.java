package school.faang.user_service.mapper.promotion;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.promotion.PromotionViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Product;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.event.EventPromotion;
import school.faang.user_service.entity.promotion.user.ProfilePromotion;

@Component
public class PromotionMapper {

    public Product toProfilePromotionProduct(PromotionPlan plan, User user) {
        ProfilePromotion profilePromotion = new ProfilePromotion();
        profilePromotion.setPrice(plan.getPrice());
        profilePromotion.setCurrency(plan.getCurrency());
        profilePromotion.setClient(user);
        profilePromotion.setProfile(user);
        profilePromotion.setNumPromotedViews(plan.getNumPromotedViews());
        profilePromotion.setViewWidth(plan.getViewWidth());
        //todo transactions
        return profilePromotion;
    }

    public Product toEventPromotionProduct(PromotionPlan plan, User client, Event event) {
        EventPromotion eventPromotion = new EventPromotion();
        eventPromotion.setPrice(plan.getPrice());
        eventPromotion.setCurrency(plan.getCurrency());
        eventPromotion.setClient(client);
        eventPromotion.setEvent(event);
        eventPromotion.setNumPromotedViews(plan.getNumPromotedViews());
        eventPromotion.setViewWidth(plan.getViewWidth());
        //todo transactions
        return eventPromotion;
    }
}
