package school.faang.user_service.dto.user;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SearchAppearanceEvent(long receiverId, long actorId, LocalDateTime viewTime) {
}
