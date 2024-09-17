package school.faang.user_service.dto.promotion;

import lombok.Builder;

@Builder
public record UserResponseDto(
        long id,
        String username,
        String promotionTariff,
        Integer numberOfViews
) {
}
