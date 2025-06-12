package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DiscriminatorColumn(
        name = "product_type",
        discriminatorType = DiscriminatorType.STRING,
        length = 50
)
@Inheritance(strategy = InheritanceType.JOINED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Convert(converter = CurrencyConverter.class)
    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

    private BigDecimal price;
}
