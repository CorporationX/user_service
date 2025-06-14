package school.faang.user_service.dto.promotion;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.promotion.enums.Plan;

import java.util.Currency;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PromotionDto {
    @NotNull(message = "Id of a User who is purchasing the promotion cannot be null")
    private Long clientId;

    @Nullable
    private Long eventId;

    @NotNull(message = "Type of promotion cannot be null")
    PromotionType promotionType;

    @NotNull(message = "Promotion plan cannot be null")
    Plan plan;

    @JsonDeserialize(using = CurrencyDeserializer.class)
    @NotNull(message = "Currency code cannot be null")
    private Currency currency;

    @AssertTrue
    public boolean isValid() {
        return promotionType == PromotionType.EVENT && eventId == null;
    }
}
