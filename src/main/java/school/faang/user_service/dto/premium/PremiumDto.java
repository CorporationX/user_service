package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PremiumDto(
        Long id,
        @Positive Long userId,
        @NotNull @Future LocalDateTime startDate,
        @NotNull @Future LocalDateTime endDate
) {
}
