package school.faang.user_service.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FollowerEventDto(
        Long followeeId,
        Long followerId,
        LocalDateTime timestamp) {
}
