package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.entity.promotion.enums.ViewWidth;

import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table(name = "promotion_plan")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan")
    private Plan plan;

    @Column(name = "num_promoted_views")
    private Integer numPromotedViews;

    @Enumerated(EnumType.STRING)
    @Column(name = "view_width")
    private ViewWidth viewWidth;

    @Convert(converter = CurrencyConverter.class)
    private Currency currency;

    private BigDecimal price;
}
