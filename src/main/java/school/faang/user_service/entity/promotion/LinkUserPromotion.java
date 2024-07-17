package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.User;

/**
 * @author Evgenii Malkov
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkUserPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private PromotionTariff tariffId;

    @Column(name = "used_action_count", nullable = false)
    private long usedActionCount;

    @Column(name = "active", nullable = false)
    private boolean active;
}
