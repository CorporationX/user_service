package school.faang.user_service.service.promotion.interfaces;

import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Product;
import school.faang.user_service.entity.promotion.PromotionPlan;

public interface PromotionActionsService {
    Product create(PromotionDto promotionDto, User client, PromotionPlan plan);

    void getItemPaid(Product product, User client);

    PromotionType getType();
}
