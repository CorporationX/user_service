package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evgenii Malkov
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PromotionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 30, nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 100)
    private String description;
}
