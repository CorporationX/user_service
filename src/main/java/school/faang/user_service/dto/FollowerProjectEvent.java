package school.faang.user_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FollowerProjectEvent(
    long followerId,
    long projectId,
    long ownerId,
    LocalDateTime eventTime
) {
}
