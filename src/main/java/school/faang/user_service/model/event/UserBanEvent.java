package school.faang.user_service.model.event;

import lombok.Builder;

@Builder
public record UserBanEvent(
        Long id
) {
}
