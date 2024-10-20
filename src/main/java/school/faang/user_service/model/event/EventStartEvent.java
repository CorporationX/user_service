package school.faang.user_service.model.event;

import lombok.Builder;

import java.util.List;

@Builder
public record EventStartEvent(long id, List<Long> userIds) {
}
