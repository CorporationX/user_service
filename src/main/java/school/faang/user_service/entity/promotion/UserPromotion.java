package school.faang.user_service.entity.promotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.Currency;

import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_promotion")
public class UserPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promotion_tariff")
    @Enumerated(EnumType.STRING)
    private PromotionTariff promotionTariff;

    @Column(name = "cost")
    private double cost;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "coefficient")
    private double coefficient;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "number_of_views")
    private int numberOfViews;

    @Column(name = "audience_reach")
    private int audienceReach;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPromotion that = (UserPromotion) o;
        return Double.compare(cost, that.cost) == 0 &&
                Double.compare(coefficient, that.coefficient) == 0 &&
                numberOfViews == that.numberOfViews &&
                audienceReach == that.audienceReach &&
                promotionTariff == that.promotionTariff &&
                currency == that.currency &&
                Objects.equals(user, that.user) &&
                Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(promotionTariff, cost, currency, coefficient, user, numberOfViews, audienceReach,
                creationDate);
    }
}
