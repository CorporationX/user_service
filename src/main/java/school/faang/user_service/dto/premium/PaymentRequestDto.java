package school.faang.user_service.dto.premium;

import lombok.Builder;

@Builder
public record PaymentRequestDto(
        Long userId,
        int days
) {
}
