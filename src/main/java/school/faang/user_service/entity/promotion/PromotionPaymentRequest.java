package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

/**
 * @author Evgenii Malkov
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionPaymentRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private long entityId;

    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private PromotionTariff tariffId;

}
