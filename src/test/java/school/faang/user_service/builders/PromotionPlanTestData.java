package school.faang.user_service.builders;

import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.entity.promotion.enums.ViewWidth;

import java.math.BigDecimal;
import java.util.Currency;

public class PromotionPlanTestData {
    private Integer id = 1;
    private Plan plan = Plan.VIP;
    private Integer numPromotedViews = 5;
    private ViewWidth viewWidth = ViewWidth.PUBLIC;
    private Currency currency = Currency.getInstance("EUR");
    private BigDecimal price = BigDecimal.valueOf(9.99);

    public static PromotionPlanTestData defaultPlan() {
        return new PromotionPlanTestData();
    }

    public PromotionPlanTestData withId(Integer id) {
        this.id = id;
        return this;
    }

    public PromotionPlanTestData withPlan(Plan plan) {
        this.plan = plan;
        return this;
    }

    public PromotionPlanTestData withNumPromotedViews(Integer numPromotedViews) {
        this.numPromotedViews = numPromotedViews;
        return this;
    }

    public PromotionPlanTestData withViewWidth(ViewWidth viewWidth) {
        this.viewWidth = viewWidth;
        return this;
    }

    public PromotionPlanTestData withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public PromotionPlanTestData withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public PromotionPlan build() {
        return PromotionPlan.builder()
                .id(id)
                .plan(plan)
                .numPromotedViews(numPromotedViews)
                .viewWidth(viewWidth)
                .currency(currency)
                .price(price)
                .build();
    }
}
