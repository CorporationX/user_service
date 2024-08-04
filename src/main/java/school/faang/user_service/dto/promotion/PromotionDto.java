package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Evgenii Malkov
 */
@Data
@AllArgsConstructor
public class PromotionDto {
    long id;
    @Positive
    @NotNull
    BigDecimal priceUsd;
    String description;
    long typeId;
    String typeDescription;
    boolean active;
}
