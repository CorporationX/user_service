package school.faang.user_service.model.dto.premium;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PremiumDto(
        @Positive
        Long id,

        @NotNull
        @Positive
        Long userId,

        @NotNull
        LocalDateTime startDate,

        @NotNull
        LocalDateTime endDate
) {
}