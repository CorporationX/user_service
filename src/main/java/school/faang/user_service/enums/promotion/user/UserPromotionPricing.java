package school.faang.user_service.enums.promotion.user;

import school.faang.user_service.enums.promotion.PromotionPriority;

import java.math.BigDecimal;

public class UserPromotionPricing {
    public static BigDecimal getPrice(int userPercentage, int feedRank) {
        UserPromotionType promotionType = UserPromotionType.fromUserPercentage(userPercentage);
        PromotionPriority priority = PromotionPriority.fromFeedRank(feedRank);
        return promotionType.getBasePrice().multiply(priority.getMultiplier());
    }
}
