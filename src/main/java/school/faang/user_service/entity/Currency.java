package school.faang.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Evgenii Malkov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;
}
