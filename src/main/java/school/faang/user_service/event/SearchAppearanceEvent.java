package school.faang.user_service.event;

import java.time.LocalDateTime;

public record SearchAppearanceEvent(
    Long userId,
    Long finderUserId,
    LocalDateTime viewDateTime
) {
}
