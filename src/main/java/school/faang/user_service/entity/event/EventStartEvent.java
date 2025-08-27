package school.faang.user_service.entity.event;

import java.util.List;

public record EventStartEvent(
        long eventId,
        List<Long> participantsIds
) {}
