package school.faang.user_service.dto.event.follower;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FollowerEventDto(
        long followerId,
        long followeeId,
        LocalDateTime subscribedAt
) {
}
