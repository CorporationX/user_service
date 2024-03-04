package school.faang.user_service.event;

import java.time.LocalDateTime;

/**
 * @author Alexander Bulgakov
 */

public record SearchAppearanceEvent(
        Long userId,
        Long xUserId,
        LocalDateTime viewingTime
) {}
