package school.faang.user_service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.enums.PremiumPeriod;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PremiumBoughtEventDto {

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotNull(message = "amount cannot be null")
    private int amount;

    @NotNull(message = "subscriptionDuration cannot be null")
    private PremiumPeriod subscriptionDuration;

    private LocalDateTime receivedAt;
}