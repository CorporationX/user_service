package school.faang.user_service.model.promotion.event;

import school.faang.user_service.model.promotion.PromotionPriority;

import java.math.BigDecimal;

public class EventPromotionPricing {
    public static BigDecimal getPrice(int userPercentage, int feedRank) {
        EventPromotionType promotionType = EventPromotionType.fromUserPercentage(userPercentage);
        PromotionPriority priority = PromotionPriority.fromFeedRank(feedRank);
        return promotionType.getBasePrice().multiply(priority.getMultiplier());
    }
}
