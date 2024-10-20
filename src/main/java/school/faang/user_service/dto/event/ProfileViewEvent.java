package school.faang.user_service.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProfileViewEvent(
        Long userId,
        Long guestId,
        LocalDateTime viewDateTime
) {
}