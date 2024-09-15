package school.faang.user_service.dto.premium;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PremiumDto(
        Long id,
        Long userId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}