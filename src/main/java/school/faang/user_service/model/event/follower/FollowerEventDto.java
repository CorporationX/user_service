package school.faang.user_service.model.event.follower;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FollowerEventDto(
        long followerId,
        long followeeId,
        LocalDateTime subscribedAt
) {
}
