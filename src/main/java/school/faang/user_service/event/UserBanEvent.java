package school.faang.user_service.event;

import lombok.Builder;

@Builder
public record UserBanEvent(
        Long id
) {
}
