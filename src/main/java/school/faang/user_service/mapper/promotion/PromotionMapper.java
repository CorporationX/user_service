package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.event.EventPromotion;
import school.faang.user_service.entity.promotion.user.ProfilePromotion;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(target = "price", source = "plan.price")
    @Mapping(target = "currency", source = "plan.currency")
    @Mapping(target = "client", source = "user")
    @Mapping(target = "plan", source = "plan.plan")
    @Mapping(target = "profile", source = "user")
    @Mapping(target = "numPromotedViews", source = "plan.numPromotedViews")
    @Mapping(target = "viewWidth", source = "plan.viewWidth")
    @Mapping(target = "transactionPurpose", constant = "PROMOTION")
    @Mapping(target = "name", constant = "User Profile promotion")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "false")
    @Mapping(target = "currentViews", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    ProfilePromotion toProfilePromotion(PromotionPlan plan, User user);

    @Mapping(target = "price", source = "plan.price")
    @Mapping(target = "currency", source = "plan.currency")
    @Mapping(target = "client", source = "client")
    @Mapping(target = "plan", source = "plan.plan")
    @Mapping(target = "numPromotedViews", source = "plan.numPromotedViews")
    @Mapping(target = "viewWidth", source = "plan.viewWidth")
    @Mapping(target = "transactionPurpose", constant = "PROMOTION")
    @Mapping(target = "name", constant = "Event promotion")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "false")
    @Mapping(target = "currentViews", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    EventPromotion toEventPromotion(PromotionPlan plan, User client, Event event);
}
