package school.faang.user_service.model.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SearchAppearanceEvent(
    Long requesterId,
    List<Long> foundUsersId,
    LocalDateTime requestDateTime
) {
}
