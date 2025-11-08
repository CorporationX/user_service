package school.faang.user_service.dto.event;

import school.faang.user_service.entity.event.EventType;

public record EventFilterDto(
        String titleContains,
        String descriptionContains,
        Long ownerId,
        Long participantId,
        EventType type
) {
}
