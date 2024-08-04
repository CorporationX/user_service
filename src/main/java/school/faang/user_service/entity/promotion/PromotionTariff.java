package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Evgenii Malkov
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PromotionTariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 30, nullable = false, unique = true)
    private String name;

    @Column(name = "price_usd", length = 30, nullable = false)
    private BigDecimal priceUsd;

    @Column(name = "description", length = 100)
    private String description;

    @ManyToOne
    @JoinColumn(name = "promotion_type", nullable = false)
    private PromotionType promotionType;

    @Column(name = "available_action_count", nullable = false)
    private long availableActionCount;

    @Column(name = "active", nullable = false)
    private boolean active;
}
