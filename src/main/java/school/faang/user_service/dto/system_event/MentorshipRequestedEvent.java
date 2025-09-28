package school.faang.user_service.dto.system_event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MentorshipRequestedEvent(Long receiverId, Long actorId, LocalDateTime createdAt) {
}