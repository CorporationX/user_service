package school.faang.user_service.dto.premium;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PremiumResponseDto(
        Long id,
        Long userId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}