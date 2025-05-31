package school.faang.user_service.service.promotion.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.promotion.enums.Plan;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProfilePromotionsViewCalculator extends PromotionsViewCalculatorBase {

    @Value("${promotion.profile.vip-view-coefficient}")
    private Double vipCoefficient;

    @Value("${promotion.profile.gold-view-coefficient}")
    private Double goldCoefficient;

    @Value("${promotion.profile.plus-view-coefficient}")
    private Double plusCoefficient;

    @Override
    public Map<Plan, Integer> calculatePromotedViews(int count) {
        Map<Plan, Integer> promotedViews = new HashMap<>();
        int vip = (int) (count * vipCoefficient);
        int gold = (int) (count * goldCoefficient);
        int plus = (int) (count * plusCoefficient);
        int normal = count - vip - gold - plus;
        promotedViews.put(Plan.VIP, vip);
        promotedViews.put(Plan.GOLD, gold);
        promotedViews.put(Plan.PLUS, plus);
        promotedViews.put(null, normal);
        return promotedViews;
    }
}
