package school.faang.user_service.dto.event;

import java.time.LocalDateTime;

/**
 * @author Alexander Bulgakov
 */

public record SearchAppearanceEventDto(
        Long receiverId,
        Long actorId,
        LocalDateTime receivedAt
) {}
