package school.faang.user_service.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SearchAppearanceEvent(
    Long userId,
    Long finderUserId,
    LocalDateTime viewDateTime
) {
}
