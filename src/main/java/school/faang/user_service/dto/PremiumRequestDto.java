package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;

public record PremiumRequestDto(
        @Min(value = 1)
        Long userId,
        @Min(value = 1)
        Long daysCount) {
}
