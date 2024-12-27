package school.faang.user_service.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventStartEvent(long eventId, String eventTitle, LocalDateTime eventStartTime, List<Long> attendeesIds) {}
