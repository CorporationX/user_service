package school.faang.user_service.service.promotion.utils;

import school.faang.user_service.entity.promotion.enums.Plan;

import java.util.Map;

public abstract class PromotionsViewCalculatorBase {
    public abstract Map<Plan, Integer> calculatePromotedViews(int size);
}
