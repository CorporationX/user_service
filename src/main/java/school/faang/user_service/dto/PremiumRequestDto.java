package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PremiumRequestDto(
        @Min(value = 1)
        @NotNull
        Long userId,
        @Min(value = 1)
        @NotNull
        Long daysCount,
        @Min(value = 1)
        @NotNull
        BigDecimal amount,
        @NotNull
        Currency currency) {
}
