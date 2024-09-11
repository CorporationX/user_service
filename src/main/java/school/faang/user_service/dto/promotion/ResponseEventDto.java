package school.faang.user_service.dto.promotion;

import lombok.Builder;

@Builder
public record ResponseEventDto(
        long id,
        String title,
        long ownerId,
        String promotionTariff,
        Integer numberOfViews
) {
}
